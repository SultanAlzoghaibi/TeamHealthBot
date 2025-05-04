package com.teamheath.bot.Commands.Users;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.User.UserService;

import java.util.List;


public class CommandMyscores implements Command {

    private final String userId;
    private final String channelId;
    private final String score;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;

    public CommandMyscores(String userId,
                           String channelId,
                           String score,
                           String responseUrl,
                           OrgService orgService, UserService userService) {
        this.userId = userId;
        this.channelId = channelId;
        this.score = score;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
    }

    @Override
    public void run() {
        // Step 1: Find the user
        UserDB matchedUser = null;

        try {
            matchedUser = userService.findBySlackUserId(userId);
            OrgDB matchedOrg = matchedUser.getOrganization();
            System.out.println("✅ Matched User Info:");
            System.out.println("Slack User ID: " + matchedUser.getSlackUserId());
            System.out.println("Role: " + matchedUser.getRole());
            System.out.println("Organization: " + (matchedOrg != null ? matchedOrg.getName() : "Unknown"));

        } catch (Exception e) {
            System.out.println("❌ Error while fetching user: " + e.getMessage());

        }
        if (matchedUser == null) {
            System.out.println("❌ User not found for Slack ID: " + userId);
        }

        // Step 3: Print all users grouped by organization
        System.out.println("📊 All Organizations & Their Users:");
        List<OrgDB> allOrgs = orgService.getAllOrganizations();
        for (OrgDB org : allOrgs) {
            System.out.println("• Org: " + org.getName() + " (Slack ID: " + org.getSlackTeamId() + ")");
            List<UserDB> users = org.getUsers();
            if (users == null || users.isEmpty()) {
                System.out.println("    ↳ No users.");
            } else {
                for (UserDB user : users) {
                    System.out.println("    ↳ User: " + user.getSlackUserId() + " | Role: " + user.getRole());
                }
            }
        }
    }



}