package com.teamheath.bot.Commands.Users;

import com.teamheath.bot.Commands.Command;

public class CommandCheckin implements Command {

    private final String userId;
    private final String channelId;
    private final String score;
    private final String responseUrl;

    public CommandCheckin(String userId, String channelId, String score, String responseUrl) {
        this.userId = userId;
        this.channelId = channelId;
        this.score = score;
        this.responseUrl = responseUrl;
    }

    @Override
    public void run() {

        System.out.println("Checkin from <@" + userId + "> with score " + score + " in " + channelId);
    }
}