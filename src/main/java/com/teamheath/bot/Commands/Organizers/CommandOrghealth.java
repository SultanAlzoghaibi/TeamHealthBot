package com.teamheath.bot.Commands.Organizers;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.Team.TeamDB;
import com.teamheath.bot.Commands.Users.Team.TeamService;
import com.teamheath.bot.Commands.Users.Team.TeamWithCountDTO;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreService;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.RedisCacheService;
import com.teamheath.bot.tools.RedisServices.RedisTeamScoreCache;
import com.teamheath.bot.tools.RedisServices.RedisUserRoleCache;

import java.util.ArrayList;
import java.util.List;

import static com.teamheath.bot.tools.Response3SecMore.response3SecMore;

public class CommandOrghealth implements Command {

    private final String userId;
    private final String channelId;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;
    private final TeamService teamService;
    private final TeamScoreService teamScoreService;
    private final RedisTeamScoreCache redisTeamScoreCache;
    private final RedisUserRoleCache redisUserRoleCache;



    public CommandOrghealth(String userId,
                            String channelId,
                            String responseUrl,
                            OrgService orgService,
                            UserService userService,
                            TeamService teamService,
                            TeamScoreService teamScoreService,
                            RedisUserRoleCache redisUserRoleCache,
                            RedisTeamScoreCache redisTeamScoreCache) {
        this.userId = userId;
        this.channelId = channelId;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
        this.teamService = teamService;
        this.teamScoreService = teamScoreService;
        this.redisTeamScoreCache = redisTeamScoreCache;
        this.redisUserRoleCache = redisUserRoleCache;
    }

    @Override
    public void run() {
        // 1. Lookup user
        System.out.println("CommandOrghealth started" );

        var userOpt = userService.findBySlackUserId(userId);

        if (userOpt.isEmpty()) {
            response3SecMore("❌ Could not find user.", responseUrl);
            return;
        }

        UserDB user = userOpt.get();

        // 2. Check role
        if (!redisUserRoleCache.isAdmin(userId)) {
            response3SecMore("🚫 Only ADMINs can reconfigure users.", responseUrl);
            return;
        }
        //response3SecMore("Amdin pass selection", responseUrl);

        // 3. Lookup organization
        OrgDB org = user.getOrganization();
        if (org == null) {
            response3SecMore("❌ You are not part of an organization.", responseUrl);
            return;
        }
        List<TeamDB> teams = org.getTeams();
        if (teams == null || teams.isEmpty()) {
            response3SecMore("ℹ️ No teams found in your organization.", responseUrl);
            return;
        }
        System.out.println("part 2 of org ");
        //4.
        StringBuilder sb = new StringBuilder("*📊 Organization Health Overview:*\n");
// 4. Get all teams with their member counts once
        List<TeamWithCountDTO> teamsWithCounts = teamService.getTeamsWithMemberCountByOrganization(org);
        if (teamsWithCounts.isEmpty()) {
            response3SecMore("ℹ️ No teams found in your organization.", responseUrl);
            return;
        }

// 5. Loop over each team
        for (TeamDB team : teams) {
            String teamName = team.getName();

            int memberCount = teamsWithCounts.stream()
                    .filter(t -> t.getTeam().getId().equals(team.getId()))
                    .map(TeamWithCountDTO::getMemberCount)
                    .findFirst()
                    .orElse(0);

            List<Integer> lastScores;
            try {
                lastScores = teamScoreService.getLastNScoresForTeam(team, 4);
            } catch (Exception e) {
                System.out.println("⚠️ Failed to fetch scores for team " + teamName + ": " + e.getMessage());
                continue;
            }

            sb.append("• *").append(teamName).append("* — ")
                    .append(memberCount).append(" member").append(memberCount == 1 ? "" : "s").append("\n");

            if (lastScores.isEmpty()) {
                sb.append("   No scores yet.\n");
            } else {
                sb.append("   Scores: ");
                for (int i = 0; i < lastScores.size(); i++) {
                    sb.append(lastScores.get(i));
                    if (i < lastScores.size() - 1) sb.append(", ");
                }
                sb.append("\n");
            }
        }

        response3SecMore(sb.toString(), responseUrl);


    }
}