package com.teamheath.bot.Commands.Organizers;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.Team.TeamService;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreService;
import com.teamheath.bot.RedisCacheService;

import static com.teamheath.bot.tools.Response3SecMore.response3SecMore;

public class CommandReconfigure implements Command {

    private final String userId;
    private final String channelId;
    private final String scoreText;
    private final String newOrgName;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;
    private final UserScoreService userScoreService;
    private final RedisCacheService redisCacheService;
    private final TeamService teamService;


    public CommandReconfigure(String userId,
                              String channelId,
                              String scoreText,
                              String newOrgName,
                              String responseUrl,
                              OrgService orgService,
                              UserService userService,
                              UserScoreService userScoreService, RedisCacheService redisCacheService, TeamService teamService) {
        this.userId = userId;
        this.channelId = channelId;
        this.scoreText = scoreText;
        this.newOrgName = newOrgName;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
        this.userScoreService = userScoreService;
        this.redisCacheService = redisCacheService;
        this.teamService = teamService;
    }

    @Override
    public void run() {
        redisCacheService.cacheUserRole("U08PCRZSQLD", "ADMIN");
        System.out.println("Cached U08PCRZSQLD as ADMIN ✅");

        if (redisCacheService.isPM(userId)) {
            response3SecMore("🚫 Only ADMINs can reconfigure users.", responseUrl);
            return;
        }
        response3SecMore("ur are a ADMIN", responseUrl);


        String[] args =  scoreText.split(" ");

        if (args.length < 2) {
            response3SecMore("⚠️ Usage: `@user PM` or `@user TEAM TeamName`", responseUrl);
            return;
        }

// Remove @ if present
        String targetUserSlackId = args[0].replaceAll("[<@>]", "").trim();

// Look up the target user in DB
        var targetUserOpt = userService.findBySlackUserId(targetUserSlackId);
        if (targetUserOpt.isEmpty()) {
            response3SecMore("❌ Could not find user: " + targetUserSlackId, responseUrl);
            return;
        }
        var targetUser = targetUserOpt.get();
        String action = args[1].toUpperCase();

        if (action.equals("PM") || action.equals("ADMIN")) {
            targetUser.setRole(action);
            userService.saveUser(targetUser);
            redisCacheService.cacheUserRole(targetUserSlackId, action);
            response3SecMore("✅ Assigned role *" + action + "* to <@" + targetUserSlackId + ">", responseUrl);
        } else if (action.equals("TEAM") && args.length >= 3) {
            String teamName = scoreText.substring(scoreText.indexOf("TEAM") + 5); // preserve full team name
            var org = targetUser.getOrganization();
            if (org == null) {
                response3SecMore("❌ Target user is not part of any organization.", responseUrl);
                return;
            }


            var teamOpt = teamService.findByNameAndOrganization(teamName, org);
            if (teamOpt.isEmpty()) {
                response3SecMore("❌ No team named `" + teamName + "` found in your org.", responseUrl);
                return;
            }
            targetUser.setTeam(teamOpt.get());
            userService.saveUser(targetUser);
            response3SecMore("✅ Moved <@" + targetUserSlackId + "> to team `" + teamName + "`", responseUrl);
        } else {
            response3SecMore("⚠️ Invalid action. Use: `@user PM`, `@user ADMIN`, or `@user TEAM TeamName`", responseUrl);
        }


        //
        //
        //
        //





        // TODO: Add logic to fetch org via user, rename org, and send Slack response

    }
}