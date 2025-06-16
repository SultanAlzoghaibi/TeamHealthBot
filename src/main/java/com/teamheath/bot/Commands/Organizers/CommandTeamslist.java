package com.teamheath.bot.Commands.Organizers;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.Team.TeamService;
import com.teamheath.bot.Commands.Users.Team.TeamWithCountDTO;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreService;
import com.teamheath.bot.tools.Response3SecMore;

import java.util.List;

import static com.teamheath.bot.tools.Response3SecMore.response3SecMore;

public class CommandTeamslist implements Command {

    private final String userId;
    private final String channelId;
    private final String responseUrl;
    private final OrgService orgService;
    private final UserService userService;
    private final TeamService teamService;

    public CommandTeamslist(String userId,
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
        // 1. Lookup user and their organization
        System.out.println("CommandTeamslist Start");
        UserDB user = userService.findBySlackId(userId);
        if (user == null) {
            response3SecMore(responseUrl, "❌ Could not find user.");
            return;
        }

        OrgDB org = user.getOrganization();
        if (org == null) {
            response3SecMore( "❌ Could not find your organization.", responseUrl);
            return;
        }

        // 2. Get list of teams in the org with member counts
        List<TeamWithCountDTO> teamsWithCounts = teamService.getTeamsWithMemberCountByOrganization(org);
        if (teamsWithCounts.isEmpty()) {
            response3SecMore( "ℹ️ No teams found in your organization.", responseUrl);
            return;
        }

        // 3. Format the message
        StringBuilder sb = new StringBuilder("Teams in your organization:\n");
        for (TeamWithCountDTO team : teamsWithCounts) {
            sb.append("- ")
                    .append(team.getTeamName())
                    .append(" — ")
                    .append(team.getMemberCount())
                    .append(" member")
                    .append(team.getMemberCount() == 1 ? "" : "s")
                    .append("\n");
        }

        // 4. Send the message to Slack
        response3SecMore(sb.toString(), responseUrl);
    }
}