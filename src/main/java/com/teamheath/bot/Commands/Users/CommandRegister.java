package com.teamheath.bot.Commands.Users;

import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreDB;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreService;
import com.teamheath.bot.tools.DBDebugger;
import com.teamheath.bot.tools.RedisServices.RedisSlackNameCache;

import java.util.Optional;

import static com.teamheath.bot.tools.DBDebugger.printAllOrgsWithUsers;
import static com.teamheath.bot.tools.Response3SecMore.response3SecMore;

public class CommandRegister implements Command {

    private final String userId;
    private final String channelId;
    private final String scoreText;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;
    private final RedisSlackNameCache redisSlackNameCache;

    public CommandRegister(String userId,
                           String channelId,
                           String scoreText,
                           String responseUrl,
                           OrgService orgService,
                           UserService userService,
                           RedisSlackNameCache redisSlackNameCache) {
        this.userId = userId;
        this.channelId = channelId;
        this.scoreText = scoreText;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
        this.redisSlackNameCache = redisSlackNameCache;
    }

    @Override
    public void run() {
        String[] parts = scoreText.trim().split("\\s+");
        printAllOrgsWithUsers(orgService);

        if (parts.length <= 2) {
            response3SecMore("❗ Usage: `/register join OrgName password123` or `/register quit OrgName password123`", responseUrl);
            return;
        }
        String action = parts[0];
        String orgName = parts[1];
        String password = parts[2];


        Optional<OrgDB> orgOpt = orgService.findByName(orgName);
        if (orgOpt.isEmpty()) {
            response3SecMore("🚫 Organization `" + orgName + "` not found.", responseUrl);
            return;
        }

        OrgDB org = orgOpt.get();

        if (!org.getPassword().equals(password)) {
            response3SecMore("🔐 Incorrect password for org `" + orgName + "`.", responseUrl);
            return;
        }


        switch (action) {
            case "join":
                userService.createUser(userId, org, "USER"); // handles user creation or upsert
                response3SecMore("✅ You’ve been registered to org *" + orgName + "*.", responseUrl);
                break;

            case "quit":
                userService.removeUserFromOrg(userId, org); // you'll implement this in UserService
                response3SecMore("👋 You’ve left org *" + orgName + "*.", responseUrl);
                break;

            default:
                response3SecMore("⚠️ Invalid action. Use `/register join OrgName password` or `/register quit OrgName password`.", responseUrl);
        }
        printAllOrgsWithUsers(orgService);



    }

}