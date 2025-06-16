package com.teamheath.bot.Commands.Organizers;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreService;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.tools.Response3SecMore;

import static com.teamheath.bot.tools.Response3SecMore.response3SecMore;

public class CommandOrghealth implements Command {

    private final String userId;
    private final String channelId;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;
    private final TeamScoreService teamScoreService;

    public CommandOrghealth(String userId,
                            String channelId,
                            String responseUrl,
                            OrgService orgService,
                            UserService userService,
                            TeamScoreService teamScoreService) {
        this.userId = userId;
        this.channelId = channelId;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
        this.teamScoreService = teamScoreService;
    }

    @Override
    public void run() {
        // TODO: 1. Lookup org from userId
        //       2. Aggregate team scores (avg, std dev, etc.)
        //       3. Format and send Slack response
    }
}