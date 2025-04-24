package com.teamheath.bot;

public class CommandCheckin implements Command {

    private final String userId;
    private final String channelId;
    private final String score;

    public CommandCheckin(String userId, String channelId, String score) {
        this.userId = userId;
        this.channelId = channelId;
        this.score = score;
    }

    @Override
    public void run() {

        System.out.println("Checkin from <@" + userId + "> with score " + score + " in " + channelId);
    }
}