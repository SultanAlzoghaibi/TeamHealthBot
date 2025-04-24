package com.teamheath.bot;

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
        Runnable create(String userId, String channelId, String scoreText);
    }

    public SlackController() {
        commandMap.put("/checkin", (u, c, s) ->
                () -> new CommandCheckin(u, c, s).run()
        );
        commandMap.put("/t2", (u, c, s) ->
                () -> System.out.println("T2 running"));
        commandMap.put("/t3", (u, c, s) -> ()
                -> System.out.println("T3 running"));
        commandMap.put("/t4", (u, c, s) ->
                () -> System.out.println("T4 running"));
    }


    @PostMapping("/commands")
    public ResponseEntity<String> handleSlashCommand(@RequestParam Map<String, String> payload) {
        String command = payload.get("command");
        String userId = payload.get("user_id");
        String channelId = payload.get("channel_id");
        String scoreText = payload.get("text");

        CommandFactory task = commandMap.get(command);
        if (task != null) {
            executor.submit(task.create(userId, channelId, scoreText));

            return ResponseEntity.ok("✅ Running `" + command + "`");
        } else {
            return ResponseEntity.badRequest().body("❌ Unknown command: `" + command + "`");
        }
    }
}