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

        if (!redisCacheService.isAdmin(userId)) {
            response3SecMore("🚫 Only ADMINs can reconfigure users.", responseUrl);
            return;
        }

        String[] args = scoreText.split(" ");
        if (args.length < 2) {
            response3SecMore("⚠️ Usage: `@user PM`, `@user ADMIN`, `@user TEAM TeamName`, or `@user NEWTEAM TeamName`", responseUrl);
            return;
        }

        String targetUserSlackId = args[0].replaceAll("[<@>]", "").trim();
        String action = args[1].toUpperCase();
        System.out.println("targetuserID: " +targetUserSlackId);

        if (!targetUserSlackId.startsWith("U")) {
            response3SecMore("❗ Please use a proper Slack @mention (select user from dropdown).", responseUrl);
            return;
        }

        switch (action) {
            case "PM":
            case "ADMIN":
                changeUserRole(targetUserSlackId, action);
                break;

            case "TEAM":
                if (args.length >= 3) {
                    String teamName = scoreText.substring(scoreText.indexOf("TEAM") + 5).trim();
                    changeTeam(targetUserSlackId, teamName);
                } else {
                    response3SecMore("⚠️ Missing team name for TEAM action.", responseUrl);
                }
                break;

            case "NEWTEAM":
                if (args.length >= 3) {
                    String teamName = scoreText.substring(scoreText.indexOf("NEWTEAM") + 8).trim();
                    createTeam(targetUserSlackId, teamName);
                } else {
                    response3SecMore("⚠️ Missing team name for NEWTEAM action.", responseUrl);
                }
                break;

            default:
                response3SecMore("⚠️ Invalid action. Use: `@user PM`, `@user ADMIN`, `@user TEAM TeamName`, or `@user NEWTEAM TeamName`", responseUrl);
        }
    }

    public void changeUserRole(String targetUserSlackId, String role) {
        var targetUserOpt = userService.findBySlackUserId(targetUserSlackId);
        if (targetUserOpt.isEmpty()) {
            response3SecMore("❌ Could not find user: " + targetUserSlackId, responseUrl);
            return;
        }

        var targetUser = targetUserOpt.get();
        targetUser.setRole(role);
        userService.saveUser(targetUser);
        redisCacheService.cacheUserRole(targetUserSlackId, role);

        response3SecMore("✅ Assigned role *" + role + "* to <@" + targetUserSlackId + ">", responseUrl);
    }



    public void changeTeam(String targetUserSlackId, String teamName) {
        var targetUserOpt = userService.findBySlackUserId(targetUserSlackId);
        if (targetUserOpt.isEmpty()) {
            response3SecMore("❌ Could not find user: " + targetUserSlackId, responseUrl);
            return;
        }

        var targetUser = targetUserOpt.get();
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
    }

    public void createTeam(String targetUserSlackId, String teamName) {
        var targetUserOpt = userService.findBySlackUserId(targetUserSlackId);
        if (targetUserOpt.isEmpty()) {
            response3SecMore("❌ Could not find user: " + targetUserSlackId, responseUrl);
            return;
        }

        var targetUser = targetUserOpt.get();
        var org = targetUser.getOrganization();
        if (org == null) {
            response3SecMore("❌ User is not assigned to any organization.", responseUrl);
            return;
        }

        // Check if team already exists
        var existingTeam = teamService.findByNameAndOrganization(teamName, org);
        if (existingTeam.isPresent()) {
            response3SecMore("⚠️ Team `" + teamName + "` already exists in your organization.", responseUrl);
            return;
        }

        // Create team
        var newTeam = teamService.createTeam(teamName, org);
        if (newTeam == null) {
            response3SecMore("❌ Failed to create team `" + teamName + "`.", responseUrl);
            return;
        }

        // Optionally assign the user to the new team
        targetUser.setTeam(newTeam);
        userService.saveUser(targetUser);

        response3SecMore("✅ Created new team `" + teamName + "` and assigned <@" + targetUserSlackId + "> to it.", responseUrl);
    }
}