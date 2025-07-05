package com.teamheath.bot.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class SlackMessenger {

    private static final String SLACK_POST_URL = "https://slack.com/api/chat.postMessage";

    private final RestTemplate restTemplate = new RestTemplate();

    private final String botToken;

    public SlackMessenger(@Value("${slack.bot.token}") String botToken) {
        this.botToken = botToken;
    }

    public void sendMessage(String userId, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(botToken); // now it's valid
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "channel", userId,
                "text", message
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(SLACK_POST_URL, entity, String.class);
        System.out.println("Slack response: " + response.getBody());
    }
}