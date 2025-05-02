package com.teamheath.bot;

import com.teamheath.bot.Commands.Users.CommandCheckin;
import com.teamheath.bot.Commands.Users.CommandMyscores;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/slack")
public class SlackController {

    private final Map<String, CommandFactory> commandMap = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10); // Tune size

    @FunctionalInterface
    public interface CommandFactory {
        Runnable create(String userId, String channelId, String scoreText, String responseURL);


    }

    public SlackController() {
        commandMap.put("/checkin", (userId, channelId, scoreText, responseURL) ->
                () -> new CommandCheckin(userId, channelId, scoreText, responseURL ).run()
        );
        commandMap.put("/myScores", (userId, channelId, scoreText, responseURL) ->
                () -> new CommandMyscores(userId, channelId, scoreText, responseURL ).run()
        );


    }


    @PostMapping("/commands")
    public ResponseEntity<String> handleSlashCommand(@RequestParam Map<String, String> payload) {
        String command = payload.get("command");
        String userId = payload.get("user_id");
        String channelId = payload.get("channel_id");
        String scoreText = payload.get("text");
        String response_url = payload.get("response_url");

    // Slack has a 3 sec timout rule

        CommandFactory task = commandMap.get(command);


        if (task != null) {
            executor.submit(task.create(userId, channelId, scoreText, response_url));

            System.out.println("RESPONSE SEND BACK");
            return ResponseEntity.ok("✅ Running `" + command + "`");
        } else {
            return ResponseEntity.badRequest().body("❌ Unknown command: `" + command + "`");
        }
    }
}