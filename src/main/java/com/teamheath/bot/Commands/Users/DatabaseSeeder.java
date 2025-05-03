package com.teamheath.bot.Commands.Users;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.User.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired private OrgService orgService;
    @Autowired private UserService userService;

    @Override
    public void run(String... args) {
        // Only seed if org doesn't already exist
        if (orgService.existsBySlackTeamId("T123456")) return;

        // Create Org
        OrgDB org = new OrgDB();
        org.setName("Alpha Org");
        org.setSlackTeamId("T123456");
        org = orgService.saveOrganization(org); // get the saved org with ID

        // Add 10 Users linked to the Org
        for (int i = 1; i <= 10; i++) {
            UserDB user = new UserDB();
            user.setSlackUserId("U" + String.format("%08d", i)); // padded like U00000001
            user.setRole("USER");
            user.setOrganization(org); // link to org
            userService.saveUser(user);
        }
    }
}