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
        List<OrgDB> allOrgs = orgService.getAllOrganizations();

        for (OrgDB org : allOrgs) {
            System.out.println("• Org: " + org.getName() + " (Slack ID: " + org.getSlackTeamId() + ")");

            List<UserDB> users = org.getUsers(); // assumes @OneToMany is working
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