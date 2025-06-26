package com.teamheath.bot.tools;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;

import java.util.List;

public class DBDebugger {

    public static void printAllOrgsWithUsers(OrgService orgService) {
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