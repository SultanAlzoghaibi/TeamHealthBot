package com.teamheath.bot.Commands.Users;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreDB;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreService;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public class CommandMyscores implements Command {

    private final String userId;
    private final String channelId;
    private final String score;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;
    private final UserScoreService userScoreService;


    public CommandMyscores(String userId,
                           String channelId,
                           String score,
                           String responseUrl,
                           OrgService orgService,
                           UserService userService,
                           UserScoreService userScoreService) {
        this.userId = userId;
        this.channelId = channelId;
        this.score = score;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
        this.userScoreService = userScoreService;
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
            if (matchedUser.getTeam() != null) {
                System.out.println("Team: " + matchedUser.getTeam().getName());
            } else {
                System.out.println("Team: None");
            }

        } catch (Exception e) {
            System.out.println("❌ Error while fetching user: " + e.getMessage());

        }
        if (matchedUser == null) {
            System.out.println("❌ User not found for Slack ID: " + userId);
        }

        System.out.println("📊 All Organizations & Their Users:");
        List<OrgDB> allOrgs = orgService.getAllOrganizations();

        for (OrgDB org : allOrgs) {
            System.out.println("• Org: " + org.getName() + " (Slack ID: " + org.getSlackTeamId() + ")");
            List<UserDB> users = org.getUsers();

            if (users == null || users.isEmpty()) {
                System.out.println("    ↳ No users.");
            } else {
                // Preload scores once for this org's users
                Map<UUID, UserScoreDB> recentScoresMap = userScoreService.getRecentScoresForUsers(users);

                for (UserDB user : users) {
                    String teamName = (user.getTeam() != null) ? user.getTeam().getName() : "No team";

                    // Lookup most recent score from the preloaded map
                    String recentScore = recentScoresMap.containsKey(user.getId())
                            ? String.valueOf(recentScoresMap.get(user.getId()).getScore())
                            : "No scores";

                    System.out.println("    ↳ User: " + user.getSlackUserId()
                            + " | Role: " + user.getRole()
                            + " | Team: " + teamName
                            + " | Recent-score: " + recentScore);
                }
            }
        }
    }



}