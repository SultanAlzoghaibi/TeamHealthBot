package com.teamheath.bot;

import com.teamhealth.grpc.ScoreServiceGrpc;
import com.teamheath.bot.Commands.Organizers.CommandOrghealth;
import com.teamheath.bot.Commands.Organizers.CommandReconfigure;
import com.teamheath.bot.Commands.Organizers.CommandTeamslist;
import com.teamheath.bot.Commands.Users.CommandCheckin;
import com.teamheath.bot.Commands.Users.CommandMyscores;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.Team.TeamService;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final ExecutorService executor = Executors.newFixedThreadPool(2); // Tune size
    private final OrgService orgService;
    private final BeanFactory applicationContext;
    private final UserScoreService userScoreService;
    private UserService userService;
    private TeamService teamService;

    @Autowired
    private final ScoreServiceGrpc.ScoreServiceBlockingStub grpcStub;

    @Autowired
    private RedisCacheService redisCacheService;

    @FunctionalInterface
    public interface CommandFactory {
        Runnable create(String userId, String channelId, String scoreText, String responseURL);


    }

    public SlackController(OrgService orgService,
                           OrgService orgService1,
                           UserService userService,
                           BeanFactory applicationContext,
                           UserScoreService userScoreService,
                           UserScoreService userScoreService1,
                           ScoreServiceGrpc.ScoreServiceBlockingStub grpcStub,
                            TeamService teamService) {
        this.orgService = orgService1;
        this.applicationContext = applicationContext;
        this.userService = userService;
        this.userScoreService = userScoreService1;
        this.grpcStub = grpcStub;
        this.teamService = teamService;

        commandMap.put("/checkin", (userId, channelId, scoreText, responseURL) ->
                () -> new CommandCheckin(userId,
                        channelId,
                        scoreText,
                        responseURL,
                        redisCacheService,
                        grpcStub).run()
        );

        commandMap.put("/myscores", (userId, channelId, scoreText, responseURL) ->
                () -> new CommandMyscores(
                        userId,
                        channelId,
                        scoreText,
                        responseURL,
                        orgService,
                        userService,
                        userScoreService
                ).run()
        );
        commandMap.put("/reconfigure", (userId, channelId, scoreText, responseURL) ->
                () -> new CommandReconfigure(
                        userId,
                        channelId,
                        scoreText,
                        responseURL,
                        orgService,
                        userService,
                        userScoreService
                ).run()
        );
        commandMap.put("/teamslist", (userId, channelId, scoreText, responseURL) ->
                () -> new CommandTeamslist(
                        userId,
                        channelId,
                        responseURL,
                        orgService,
                        userService,
                        teamService // ✅ correct service
                ).run()
        );
        commandMap.put("/orghealth", (userId, channelId, scoreText, responseURL) ->
                () -> new CommandOrghealth(
                        userId,
                        channelId,
                        responseURL,
                        orgService,
                        userService,
                        teamService // ✅ correct service
                ).run()
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
        System.out.println("user_id: "+ userId);

        CommandFactory task = commandMap.get(command);

        if (task != null) {
            executor.submit(task.create(userId, channelId, scoreText, response_url));

            System.out.println("RESPONSE SEND BACK");
            return ResponseEntity.ok("✅ received `" + command + "| wait for response now...");
        } else {
            return ResponseEntity.badRequest().body("❌ Unknown command: `" + command + "`");
        }
    }
}