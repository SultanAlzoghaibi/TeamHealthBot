package com.teamheath.bot.Commands.Users;

import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.RedisCacheService;

public class CommandCheckin implements Command {
    private final RedisCacheService redisCacheService;

    private final String userId;
    private final String channelId;
    private final String score;
    private final String responseUrl;

    private int fill = 0;

    public CommandCheckin(String userId,
                          String channelId,
                          String score,
                          String responseUrl,
                          RedisCacheService redisCacheService) {
        this.userId = userId;
        this.channelId = channelId;
        this.score = score;
        this.responseUrl = responseUrl;
        this.redisCacheService = redisCacheService;

    }

    @Override
    public void run() {
        // Look up teamId by userId (e.g., from DB or service)
        String teamId = getTeamIdFromUser(userId); // implement this

        // Check if user already submitted
        if (redisCacheService.hasCheckedIn(teamId, userId)) {
            System.out.println("User <@" + userId + "> already checked in. Ignoring duplicate.");
            return;
        }

        // Mark check-in in Redis (with TTL)
        redisCacheService.markCheckedIn(teamId, userId);

        System.out.println("✅ Checkin from <@" + userId + "> with score " + score + " in " + channelId);

        fill++;
        System.out.println("fill " + fill);

        if (fill > 2) {
            System.out.println("🚀 run gRPC");
            // TODO: gRPC scoring call here
        }
    }

    private String getTeamIdFromUser(String userId) {
        // Temporary stub: replace with actual DB or userService call
        return "T12346"; // Just hardcode for testing
    }
}