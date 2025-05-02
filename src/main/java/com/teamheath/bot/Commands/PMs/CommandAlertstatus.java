package com.teamheath.bot.Commands.PMs;
import com.teamheath.bot.Commands.Command;

public class CommandAlertstatus implements Command {

    private final String userId;
    private final String channelId;
    private final String score;
    private final String responseUrl;

    public CommandAlertstatus(String userId, String channelId, String score, String responseUrl) {
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