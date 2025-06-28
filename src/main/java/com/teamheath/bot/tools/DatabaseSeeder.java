package com.teamheath.bot.tools;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.Team.TeamDB;
import com.teamheath.bot.Commands.Users.Team.TeamService;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreDB;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreService;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreDB;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired private OrgService orgService;
    @Autowired private UserService userService;
    @Autowired private TeamService teamService;
    @Autowired private UserScoreService userScoreService;
    @Autowired private TeamScoreService teamScoreService;

    @Override
    public void run(String... args) {
        // Only seed if org doesn't already exist

        if (orgService.existsBySlackTeamId("T123456")) {
            System.out.println("You already have an organisation team");
            return;
        }
        System.out.println("Creating a new organisation");
        // Create Org
        OrgDB org = new OrgDB();
        org.setName("Alpha Org");
        org.setSlackTeamId("T123456");
        org = orgService.saveOrganization(org); // get the saved org with ID

        TeamDB team1 = new TeamDB();
        team1.setName("Team A");
        team1.setOrganization(org);
        team1 = teamService.saveTeam(team1); // assuming teamService exists

        TeamDB team2 = new TeamDB();
        team2.setName("Team B");
        team2.setOrganization(org);
        team2 = teamService.saveTeam(team2);

        // Add 10 Users linked to the Org
        for (int i = 1; i <= 10; i++) {
            UserDB user = new UserDB();
            user.setSlackUserId("U" + String.format("%08d", i));
            if (i == 1) user.setSlackUserId("P08PCRZSQLD");
            user.setRole("ADMIN");
            user.setOrganization(org);
            user.setTeam(i <= 5 ? team1 : team2); // Assign team
            userService.saveUser(user);

            for (int j = 1; j <= 5; j++) {
                UserScoreDB score = new UserScoreDB();
                score.setUser(user);
                score.setTeam(user.getTeam());
                score.setScore((int) (83+i)); // Random score 0–10
                userScoreService.saveScore(score); // ✅ ACTUALLY SAVE IT
            }

        }
        // Add a team-level score for each team
        // Add multiple team-level scores for each team
        int[] scoresTeamA = {85, 83, 88, 84};
        int[] scoresTeamB = {90, 87, 89, 91};

        for (int score : scoresTeamA) {
            TeamScoreDB teamScore = new TeamScoreDB();
            teamScore.setTeam(team1);
            teamScore.setScore(score);
            teamScoreService.saveTeamScore(teamScore);
        }

        for (int score : scoresTeamB) {
            TeamScoreDB teamScore = new TeamScoreDB();
            teamScore.setTeam(team2);
            teamScore.setScore(score);
            teamScoreService.saveTeamScore(teamScore);
        }
    }
}