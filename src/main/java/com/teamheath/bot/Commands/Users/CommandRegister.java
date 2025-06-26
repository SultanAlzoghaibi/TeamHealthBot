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
import com.teamheath.bot.tools.RedisServices.RedisUserRoleCache;

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
    private final RedisUserRoleCache redisUserRoleCache;

    public CommandRegister(String userId,
                           String channelId,
                           String scoreText,
                           String responseUrl,
                           OrgService orgService,
                           UserService userService,
                           RedisSlackNameCache redisSlackNameCache,
                           RedisUserRoleCache redisUserRoleCache
                           ) {
        this.userId = userId;
        this.channelId = channelId;
        this.scoreText = scoreText;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
        this.redisSlackNameCache = redisSlackNameCache;
        this.redisUserRoleCache = redisUserRoleCache;

    }

    @Override
    public void run() {
        String[] parts = scoreText.trim().split("\\s+");
        printAllOrgsWithUsers(orgService); // Optional: debug tool

        if (parts.length != 3) {
            response3SecMore("❗ Usage: `/register join OrgName password123` or `/register quit OrgName password123`", responseUrl);
            return;
        }

        String action = parts[0].toLowerCase();
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
                Optional<UserDB> existing = userService.findBySlackUserIdAndOrganization(userId, org);

                if (existing.isPresent()) {
                    response3SecMore("📎 You’re already registered in org *" + org.getName() + "*.", responseUrl);
                    return;
                }
                userService.createUser(userId, org, "USER"); // Creates user with USER role
                response3SecMore("✅ You’ve been registered to org *" + orgName + "*.", responseUrl);
                break;

            case "quit":
                userService.removeUserFromOrg(userId, org);
                redisUserRoleCache.cacheUserRole(userId, null); // also clear their role from Redis
                response3SecMore("👋 You’ve left org *" + orgName + "*.", responseUrl);
                break;

            default:
                response3SecMore("⚠️ Invalid action. Use `/register join OrgName password123` or `/register quit OrgName password123`.", responseUrl);
        }

        printAllOrgsWithUsers(orgService); // Optional: debug tool
    }

}