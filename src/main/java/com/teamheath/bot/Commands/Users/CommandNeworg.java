package com.teamheath.bot.Commands.Users;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.tools.RedisServices.RedisUserRoleCache;

import java.util.List;
import java.util.Optional;

import static com.teamheath.bot.tools.Response3SecMore.response3SecMore;

public class CommandNeworg implements Command {

    private final String userId;
    private final String channelId;
    private final String scoreText;
    private final String slackTeamId;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;
    private final RedisUserRoleCache redisUserRoleCache;



    public CommandNeworg(String userId,
                         String channelId,
                         String scoreText,
                         String responseUrl,
                         OrgService orgService,
                         UserService userService,
                         RedisUserRoleCache redisUserRoleCache) {

        this.userId = userId;
        this.channelId = channelId;
        this.scoreText = scoreText;
        this.responseUrl = responseUrl;
        this.slackTeamId = scoreText;
        this.orgService = orgService;
        this.userService = userService;
        this.redisUserRoleCache = redisUserRoleCache;
    }

    @Override
    public void run() {
        printAllOrgsWithUsers();

        String[] parts = scoreText.trim().split("\\s+");
        if (parts.length < 2) {
            response3SecMore("❗ Usage:\n• `/neworg OrgName password123` to create\n• `/neworg DEL OrgName password123` to delete", responseUrl);
            return;
        }

        boolean isDelete = parts[0].equalsIgnoreCase("DEL");

        if (isDelete) {
            if (parts.length < 3) {
                response3SecMore("❗ Usage: `/neworg DEL OrgName password123`", responseUrl);
                return;
            }
            String orgName = parts[1];
            String password = parts[2];

            try {
                Optional<OrgDB> orgOpt = orgService.findByName(orgName);
                if (orgOpt.isEmpty()) {
                    response3SecMore("🚫 Org not found: `" + orgName + "`", responseUrl);
                    return;
                }

                OrgDB org = orgOpt.get();

                // Verify password
                if (!org.getPassword().equals(password)) {
                    response3SecMore("❌ Incorrect password for org `" + orgName + "`", responseUrl);
                    return;
                }

                // Only allow ADMINs to delete
                if (!redisUserRoleCache.isAdmin(userId)) {
                    response3SecMore("🚫 Only ADMINs can delete the organization.", responseUrl);
                    return;
                }

                // Uncache all user roles
                org.getUsers().forEach(user -> {
                    redisUserRoleCache.removeUserRole(user.getSlackUserId()); // ✅ good
                });

                orgService.deleteOrg(org);
                response3SecMore("🗑️ Org *" + orgName + "* deleted with all data.", responseUrl);
            } catch (Exception e) {
                response3SecMore("⚠️ Delete failed: " + e.getMessage(), responseUrl);
            }

        } else {
            // CREATE ORG FLOW
            String orgName = parts[0];
            String password = parts[1];

            try {
                OrgDB newOrg = orgService.createOrg(orgName, password, slackTeamId);
                userService.createUser(userId, newOrg);
                redisUserRoleCache.cacheUserRole(userId, "ADMIN");
                redisUserRoleCache.cacheUserRole(userId, "ADMIN");
                response3SecMore("✅ Created new organization *" + orgName + "*.\nYou are now the admin.", responseUrl);
            } catch (Exception e) {
                response3SecMore("🚫 Failed to create organization. " + e.getMessage(), responseUrl);
            }
        }
        printAllOrgsWithUsers();



    }


    public void printAllOrgsWithUsers() {
        List<OrgDB> allOrgs = orgService.findAll();
        if (allOrgs.isEmpty()) {
            System.out.println("❗ No organizations found.");
            return;
        }

        System.out.println("📊 All Organizations and Users:");
        for (OrgDB org : allOrgs) {
            System.out.println("— Org: " + org.getName() + " (SlackTeamId: " + org.getSlackTeamId() + ")");
            if (org.getUsers().isEmpty()) {
                System.out.println("    (No users)");
            } else {
                for (UserDB user : org.getUsers()) {
                    System.out.println("    • User ID: " + user.getSlackUserId() + ", Role: " + user.getRole());
                }
            }
        }
    }
}