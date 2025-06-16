package com.teamheath.bot.tools;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
public class Response3SecMore {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static void response3SecMore(String message, String responseUrl) {
        try {
            String json = "{ \"text\": \"" + escapeJson(message) + "\" }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(responseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ Slack follow-up response sent.");
        } catch (Exception e) {
            System.out.println("❌ Failed to send response to Slack: " + e.getMessage());
        }
    }

    private static String escapeJson(String raw) {
        return raw
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}