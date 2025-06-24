package com.teamheath.bot.Commands.PMs;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Organizers.CommandOrghealth;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.Team.TeamService;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreService;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.RedisCacheService;
import com.teamheath.bot.tools.RedisServices.RedisTeamScoreCache;
import com.teamheath.bot.tools.RedisServices.RedisUserRoleCache;

import java.util.List;

import static com.teamheath.bot.tools.Response3SecMore.response3SecMore;

public class CommandMyteamsscores implements Command {

    private final String userId;
    private final String channelId;
    private final String responseUrl;

    private final OrgService orgService;
    private final UserService userService;
    private final TeamService teamService;
    private final TeamScoreService teamScoreService;
    private final RedisUserRoleCache redisUserRoleCache;
    private final RedisTeamScoreCache redisTeamScoreCache;

    public CommandMyteamsscores(
            String userId,
            String channelId,
            String responseUrl,
            OrgService orgService,
            UserService userService,
            TeamService teamService,
            TeamScoreService teamScoreService,
            RedisUserRoleCache redisUserRoleCache,
            RedisTeamScoreCache redisTeamScoreCache
            ) {
        this.userId = userId;
        this.channelId = channelId;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
        this.teamService = teamService;
        this.teamScoreService = teamScoreService;
        this.redisUserRoleCache = redisUserRoleCache;
        this.redisTeamScoreCache = redisTeamScoreCache;

    }

    @Override
    public void run() {
        System.out.println("Myteamsscores started");
        var userOpt = userService.findBySlackUserId(userId);
        if (userOpt.isEmpty()) {
            response3SecMore("❌ User not found.", responseUrl);
            return;
        }

        var user = userOpt.get();

        if (user.getRole().equalsIgnoreCase("USER")) {
            response3SecMore("🚫 Only PMs can access team scores.", responseUrl);
            return;
        }

        if (user.getRole().equalsIgnoreCase("ADMIN")) {
            response3SecMore("You are an ADMIN, not a PM", responseUrl);

            new CommandOrghealth(
                            userId,
                            channelId,
                            responseUrl,
                            orgService,
                            userService,
                            teamService, // ✅ correct service
                            teamScoreService,
                            redisUserRoleCache,
                            redisTeamScoreCache
                    ).run();
            return;
        }




        var team = user.getTeam();
        if (team == null) {
            response3SecMore("❌ No team assigned to you.", responseUrl);
            return;
        }

        String teamKey = "team:" + team.getId() + ":last4";
        List<String> cachedScores = redisTeamScoreCache.getLast4TeamScores(teamKey);

        if (cachedScores == null || cachedScores.isEmpty()) {
            // fallback to PostgreSQL
            List<Integer> scores = teamScoreService.getLastNScoresForTeam(team, 4);

            if (scores.isEmpty()) {
                response3SecMore("ℹ️ No scores found for your team.", responseUrl);
                return;
            }

            // Save to Redis
            redisTeamScoreCache.cacheLast4TeamScores(teamKey, scores);
            cachedScores = scores.stream().map(String::valueOf).toList();
        }

        StringBuilder msg = new StringBuilder("📊 Last 4 scores for your team:\n");
        for (String score : cachedScores) {
            msg.append("- ").append(score).append("\n");
        }

        response3SecMore(msg.toString(), responseUrl);
    }
}
