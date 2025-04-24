package com.teamheath.bot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/slack")
public class SlackController {

    @PostMapping("/commands")
    public ResponseEntity<String> handleSlashCommand(@RequestParam Map<String, String> payload) {
        String scoreText = payload.get("text"); // e.g., "8.32"
        String userId = payload.get("user_id");
        String channelId = payload.get("channel_id");

        // Parse score


        // Store in DB, cache in Redis, send to gRPC, etc...
        System.out.println("from channel: " + channelId + "score: " + scoreText + ", <@" + userId + ">");
        return ResponseEntity.ok("✅ Received your score of " + scoreText + ", <@" + userId + ">");
    }
}