package com.teamheath.bot.Commands.Organizers;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreService;

public class CommandReconfigure implements Command {

    private final String userId;
    private final String channelId;
    private final String newOrgName;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;
    private final UserScoreService userScoreService;

    public CommandReconfigure(String userId,
                              String channelId,
                              String newOrgName,
                              String responseUrl,
                              OrgService orgService,
                              UserService userService,
                              UserScoreService userScoreService) {
        this.userId = userId;
        this.channelId = channelId;
        this.newOrgName = newOrgName;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
        this.userScoreService = userScoreService;
    }

    @Override
    public void run() {
        // TODO: Add logic to fetch org via user, rename org, and send Slack response

    }
}