package com.teamheath.bot.Commands.Users.Team;

public class TeamWithCountDTO {
    private String teamName;
    private int memberCount;

    public TeamWithCountDTO(String teamName, int memberCount) {
        this.teamName = teamName;
        this.memberCount = memberCount;
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
// getters
}