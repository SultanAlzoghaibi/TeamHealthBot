package com.teamheath.bot.Commands.Users;

import com.teamhealth.grpc.ScoreProto;
import com.teamhealth.grpc.ScoreServiceGrpc;
import com.teamheath.bot.Commands.Command;
import com.teamheath.bot.RedisCacheService;

//gRPC
import com.teamhealth.grpc.ScoreProto.CalculateScoreRequest;
import com.teamhealth.grpc.ScoreProto.CalculateScoreResponse;
import com.teamhealth.grpc.ScoreProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

public class CommandCheckin implements Command {
    private final RedisCacheService redisCacheService;

    private final String userId;
    private final String channelId;
    private final String score;
    private final String responseUrl;

    private final ScoreServiceGrpc.ScoreServiceBlockingStub grpcStub;


    private static int fill = 0;

    public CommandCheckin(String userId,
                          String channelId,
                          String score,
                          String responseUrl,
                          RedisCacheService redisCacheService,
                          ScoreServiceGrpc.ScoreServiceBlockingStub grpcStub) {
        this.userId = userId;
        this.channelId = channelId;
        this.score = score;
        this.responseUrl = responseUrl;
        this.redisCacheService = redisCacheService;
        this.grpcStub = grpcStub;
    }

    @Override
    public void run() {
        System.out.println("run checkin");
        // Look up teamId by userId (e.g., from DB or service)
        // 1. Get teamId (you'll replace this with a real DB/service lookup)
        String teamId = getTeamIdFromUser(userId);
        String orgId = getOrgIdFromTeamId(teamId); // Add this if nesting by org

        // Check if user already submitted
        if (redisCacheService.hasCheckedIn(orgId, teamId, userId)) {
            System.out.println("User <@" + userId + "> already checked in. INGORIRNG duplicate.");
            return;
        }
        try {
            int numericScore = Integer.parseInt(score);
            // Optional: Add validation range check (e.g., 1–10)
            if (numericScore <= 1 || numericScore >= 100) {
                System.out.println("❌ Invalid score range. Must be between 1 and 10.");
                return;
            }
            redisCacheService.cacheScore(orgId, teamId, userId, numericScore);

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid score format. Must be a number.");
        }

        System.out.println("✅ Checkin from <@" + userId + "> with score " + score + " in " + channelId);
        fill++;
        System.out.println("fill " + fill);


        sendScoresViaGRPC(orgId, teamId);


    }

    private String getTeamIdFromUser(String userId) {
        // Temporary stub:
        return "0b50b8b5-a4ee-4ec8-a78e-3269f64e1bfe"; // Just hardcode for testing
    }

    private String getOrgIdFromTeamId(String teamId) {
        // Temporary stub:
        return "d21c3a04-adc4-4ab1-a60b-71125819faa0"; // Just hardcode for testing
    }

    private void sendScoresViaGRPC(String orgId, String teamId) {
        System.out.println("🚀 run gRPC");
        // 1. Load scores from Redis

        Map<String, String> scores = redisCacheService.getTeamScores(orgId, teamId);
        // 2. Build gRPC request
        CalculateScoreRequest.Builder requestBuilder = CalculateScoreRequest.newBuilder()
                .setTeamId(teamId);
        for (Map.Entry<String, String> entry : scores.entrySet()) {
            try {
                System.out.println("Key: " + entry.getKey() + "| " +"value: " + entry.getValue());
                int score = Integer.parseInt(entry.getValue());
                requestBuilder.putScores(entry.getKey(), score);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid score for user: " + entry.getKey());
            }
        }
        CalculateScoreRequest request = requestBuilder.build();

        // 3. Send request to gRPC service
        try {

            long start = System.nanoTime();
            CalculateScoreResponse response = grpcStub.calculateScore(request);
            long end = System.nanoTime();

            System.out.println("🕒 gRPC call took " + (end - start) + " ns");

            System.out.println("✅ Final team score: " + response.getFinalScore());
        } catch (Exception e) {
            System.out.println("❌ gRPC call failed: " + e.getMessage());
            e.printStackTrace();
        }

    }

}