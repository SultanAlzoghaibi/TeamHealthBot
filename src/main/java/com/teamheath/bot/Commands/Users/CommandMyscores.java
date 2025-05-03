package com.teamheath.bot.Commands.Users;

import com.teamheath.bot.Commands.Command;

import java.util.List;


public class CommandMyscores implements Command {

    private final String userId;
    private final String channelId;
    private final String score;
    private final String responseUrl;
    private final OrgService orgService;

    public CommandMyscores(String userId,
                           String channelId,
                           String score,
                           String responseUrl,
                           OrgService orgService) {
        this.userId = userId;
        this.channelId = channelId;
        this.score = score;
        this.responseUrl = responseUrl;
        this.orgService = orgService;
    }

    @Override
    public void run() {
        if (!orgService.existsBySlackTeamId("T123456")) {
            OrgDB org1 = new OrgDB();
            org1.setName("Alpha Org");
            org1.setSlackTeamId("T123456");
            orgService.saveOrganization(org1);
        }

        if (!orgService.existsBySlackTeamId("T654321")) {
            OrgDB org2 = new OrgDB();
            org2.setName("Beta Org");
            org2.setSlackTeamId("T654321");
            orgService.saveOrganization(org2);
        }

        List<OrgDB> allOrgs = orgService.getAllOrganizations();
        allOrgs.forEach(org -> System.out.println("Org: " + org.getName()));
        System.out.println("no orgd");
    }
}