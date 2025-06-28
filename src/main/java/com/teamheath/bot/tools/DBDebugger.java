package com.teamheath.bot.tools;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Team.TeamDB;
import com.teamheath.bot.Commands.Users.Team.TeamService;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public static void printAllTeams(TeamService teamService) {
        List<TeamDB> teams = teamService.findAllTeams(); // or use a teamRepository method

        System.out.println("📌 All Teams by Organization:");
        for (TeamDB team : teams) {
            System.out.println(team.getName());
        }
    }


}