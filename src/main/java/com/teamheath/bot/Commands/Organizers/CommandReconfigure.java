package com.teamheath.bot.Commands.Organizers;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.Team.TeamService;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreService;
import com.teamheath.bot.RedisCacheService;
import com.teamheath.bot.tools.CacheDebugger;
import com.teamheath.bot.tools.DBDebugger;
import com.teamheath.bot.tools.RedisServices.RedisSlackNameCache;
import com.teamheath.bot.tools.RedisServices.RedisTeamScoreCache;
import com.teamheath.bot.tools.RedisServices.RedisUserRoleCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static com.teamheath.bot.tools.Response3SecMore.response3SecMore;

public class CommandReconfigure implements Command {

    private final String userId;
    private final String channelId;
    private final String scoreText;
    private final String newOrgName;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;
    private final TeamService teamService;
    private final UserScoreService userScoreService;
    private final RedisUserRoleCache redisUserRoleCache;
    private final RedisTeamScoreCache redisTeamScoreCache;
    private final RedisSlackNameCache redisSlackNameCache;


    public CommandReconfigure(String userId,
                              String channelId,
                              String scoreText,
                              String newOrgName,
                              String responseUrl,
                              OrgService orgService,
                              UserService userService,
                              TeamService teamService,
                              UserScoreService userScoreService,
                              RedisUserRoleCache redisUserRoleCache,
                              RedisTeamScoreCache redisTeamScoreCache,
                              RedisSlackNameCache redisSlackNameCache
                              ) {
        this.userId = userId;
        this.channelId = channelId;
        this.scoreText = scoreText;
        this.newOrgName = newOrgName;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
        this.userScoreService = userScoreService;
        this.redisUserRoleCache = redisUserRoleCache;
        this.redisTeamScoreCache = redisTeamScoreCache;
        this.redisSlackNameCache = redisSlackNameCache;
        this.teamService = teamService;
    }

    // @user PM             → Assigns PM role to user
    // @user ADMIN          → Assigns ADMIN role to user
    // @user TEAM TeamName  → Moves user to existing team
    // @user NEWTEAM TeamName → Creates new team and assigns user to it
    @Override
    public void run() {

        redisSlackNameCache.cacheSlackNameToId(
                "338a0026-26b4-4df1-80ca-2db0efa05c04",
                "ssultan2800",
                "U08RNSS83V4" ) ;
        CacheDebugger.printAllRedisCache();



        try {
            if (!redisUserRoleCache.isAdmin(userId)) {
                response3SecMore("🚫 Only ADMINs can reconfigure users.", responseUrl);
                return;
            }

            String[] args = scoreText.split(" ");
            if (args.length < 2) {
                response3SecMore("⚠️ Usage: `@user PM`, `@user ADMIN`, `@user TEAM TeamName`, `@user NEWTEAM TeamName`, or `@user DELTEAM TeamName`", responseUrl);
                return;
            }

            String displayName = args[0].replaceAll("[<@>]", "").trim();
            String action = args[1].toUpperCase();

            // Get orgId of the requesting admin
            var requestingUserOpt = userService.findBySlackUserId(userId);
            if (requestingUserOpt.isEmpty()) {
                response3SecMore("❌ Cannot resolve your organization.", responseUrl);
                return;
            }
            String orgId = requestingUserOpt.get().getOrgSlackId();



            // Look up actual Slack ID from display name
            Optional<String> targetSlackIdOpt = redisSlackNameCache.getUserIdFromSlackName(orgId, displayName);
            if (targetSlackIdOpt.isEmpty()) {
                response3SecMore("❌ Could not find Slack user for display name: " + displayName, responseUrl);
                return;
            }
            String targetUserSlackId = targetSlackIdOpt.get();
            System.out.println("Resolved user ID: " + targetUserSlackId);
            ;


            if (!targetUserSlackId.startsWith("U")) {
                response3SecMore("❗ Please use a proper Slack @mention (select user from dropdown).", responseUrl);
                return;
            }

            switch (action) {
                case "PM", "ADMIN", "USER":
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

                case "DELTEAM":
                    if (args.length >= 3) {
                        String teamName = scoreText.substring(scoreText.indexOf("DELTEAM") + 8).trim();
                        deleteTeam(teamName);
                    } else {
                        response3SecMore("⚠️ Missing team name for DELTEAM action.", responseUrl);
                    }
                    break;


                default:
                    response3SecMore("⚠️ Invalid action. Use: `@user PM`, `@user ADMIN`, `@user TEAM TeamName`, or `@user NEWTEAM TeamName`", responseUrl);
            }
        } catch (Exception e) {
            DBDebugger.printAllOrgsWithUsers(orgService);
            DBDebugger.printAllTeams(teamService);
            response3SecMore("⚠\uFE0F Usage: `@user PM`, `@user ADMIN`, `@user TEAM TeamName`, or `@user NEWTEAM TeamName`\", responseUrl", responseUrl);

            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        DBDebugger.printAllOrgsWithUsers(orgService);
        DBDebugger.printAllTeams(teamService);

    }

    public void deleteTeam(String teamName) {
        var orgOpt = userService.findBySlackUserId(userId);
        if (orgOpt.isEmpty()) {
            response3SecMore("❌ Could not identify your org.", responseUrl);
            return;
        }

        var org = orgOpt.get().getOrganization();
        if (org == null) {
            response3SecMore("❌ You're not part of any organization.", responseUrl);
            return;
        }

        var teamOpt = teamService.findByNameAndOrganization(teamName, org);
        if (teamOpt.isEmpty()) {
            response3SecMore("❌ Team `" + teamName + "` not found in your organization.", responseUrl);
            return;
        }

        var team = teamOpt.get();
        teamService.deleteTeam(team); // or deleteById if needed
        response3SecMore("🗑️ Deleted team `" + teamName + "` from your org.", responseUrl);
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
        redisUserRoleCache.cacheUserRole(targetUserSlackId, role);

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