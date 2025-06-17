package com.teamheath.bot.Commands.Organizers;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.Team.TeamService;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreService;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.User.UserService;

import static com.teamheath.bot.tools.Response3SecMore.response3SecMore;

public class CommandOrghealth implements Command {

    private final String userId;
    private final String channelId;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;
    private final TeamService teamService;

    public CommandOrghealth(String userId,
                            String channelId,
                            String responseUrl,
                            OrgService orgService,
                            UserService userService,
                            TeamService teamService) {
        this.userId = userId;
        this.channelId = channelId;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
        this.userService = userService;
        this.teamService = teamService;
    }

    @Override
    public void run() {
        // 1. Lookup user
        System.out.println("CommandOrghealth started" );

        var userOpt = userService.findBySlackUserId(userId);

        if (userOpt.isEmpty()) {
            response3SecMore("❌ Could not find user.", responseUrl);
            return;
        }

        UserDB user = userOpt.get();

        // 2. Check role
        if (!user.getRole().equalsIgnoreCase("PM") && !user.getRole().equalsIgnoreCase("ADMIN")) {
            response3SecMore("🚫 You must be a PM or Admin to view organization health.", responseUrl);
            return;
        }
        response3SecMore("Amdin pass selection", responseUrl);

        // TODO: 1. Lookup org from userId
        //       2. Aggregate team scores (avg, std dev, etc.)
        //       3. Format and send Slack response
    }
}