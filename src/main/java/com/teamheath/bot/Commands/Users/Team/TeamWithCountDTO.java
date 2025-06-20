package com.teamheath.bot.Commands.Users.Team;

import com.teamheath.bot.Commands.Users.Org.OrgDB;

public class TeamWithCountDTO {
    private String teamName;
    private int memberCount;
    private TeamDB team;

    public TeamWithCountDTO(String teamName, int memberCount, TeamDB team) {
        this.teamName = teamName;
        this.memberCount = memberCount;
        this.team = team;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public TeamDB getTeam() {
        return team;
    }

// getters
}