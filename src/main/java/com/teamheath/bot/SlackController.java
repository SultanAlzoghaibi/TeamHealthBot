package com.teamheath.bot;

import com.teamhealth.grpc.ScoreServiceGrpc;
import com.teamheath.bot.Commands.Organizers.CommandOrghealth;
import com.teamheath.bot.Commands.Organizers.CommandReconfigure;
import com.teamheath.bot.Commands.Organizers.CommandTeamslist;
import com.teamheath.bot.Commands.PMs.CommandMyteamsscores;
import com.teamheath.bot.Commands.Users.CommandCheckin;
import com.teamheath.bot.Commands.Users.CommandMyscores;
import com.teamheath.bot.Commands.Users.CommandNeworg;
import com.teamheath.bot.Commands.Users.CommandRegister;
import com.teamheath.bot.Commands.Users.Org.OrgService;
import com.teamheath.bot.Commands.Users.Team.TeamService;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreService;
import com.teamheath.bot.Commands.Users.User.UserService;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreService;
import com.teamheath.bot.tools.RedisServices.RedisCheckinCache;
import com.teamheath.bot.tools.RedisServices.RedisSlackNameCache;
import com.teamheath.bot.tools.RedisServices.RedisTeamScoreCache;
import com.teamheath.bot.tools.RedisServices.RedisUserRoleCache;
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
    private final TeamScoreService teamScoreService;


    @Autowired
    private final ScoreServiceGrpc.ScoreServiceBlockingStub grpcStub;

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired private RedisUserRoleCache redisUserRoleCache;
    @Autowired private RedisTeamScoreCache redisTeamScoreCache;
    @Autowired private RedisCheckinCache redisCheckinCache;
    @Autowired private RedisSlackNameCache redisSlackNameCache;



    @FunctionalInterface
    public interface CommandFactory {
        Runnable create(String userId,
                        String channelId,
                        String scoreText,
                        String responseURL);
    }

    @Autowired
    public SlackController(OrgService orgService,
                           UserService userService,
                           BeanFactory applicationContext,
                           UserScoreService userScoreService,
                           UserScoreService userScoreService1,
                           ScoreServiceGrpc.ScoreServiceBlockingStub grpcStub,
                           TeamService teamService,
                           TeamScoreService teamScoreService
        ) {
        this.orgService = orgService;
        this.applicationContext = applicationContext;
        this.userService = userService;
        this.userScoreService = userScoreService1;
        this.grpcStub = grpcStub;
        this.teamService = teamService;
        this.teamScoreService = teamScoreService;


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
                        scoreText, // this is the missing newOrgName
                        responseURL,
                        orgService,
                        userService,
                        teamService,
                        userScoreService,
                        redisUserRoleCache,
                        redisTeamScoreCache,
                        redisSlackNameCache

                ).run()
        );

        commandMap.put("/teamslist", (userId, channelId, scoreText, responseURL) ->
                () -> new CommandTeamslist(
                        userId,
                        channelId,
                        responseURL,
                        orgService,
                        userService,
                        teamService,
                        redisUserRoleCache

                ).run()
        );
        commandMap.put("/orghealth", (userId, channelId, scoreText, responseURL) ->
                () -> new CommandOrghealth(
                        userId,
                        channelId,
                        responseURL,
                        orgService,
                        userService,
                        teamService, // ✅ correct service
                        teamScoreService,
                        redisUserRoleCache,
                        redisTeamScoreCache


                ).run()
        );
        commandMap.put("/myteamsscores", (userId, channelId, scoreText, responseURL) ->
                () -> new CommandMyteamsscores(
                        userId,
                        channelId,
                        responseURL,
                        orgService,
                        userService,
                        teamService, // ✅ correct service
                        teamScoreService,
                        redisUserRoleCache,
                        redisTeamScoreCache

                ).run()
        );

        commandMap.put("/register", (userId, channelId, scoreText, responseUrl) ->
                () -> new CommandRegister(
                        userId,
                        channelId,
                        scoreText,
                        responseUrl,
                        orgService,
                        userService,
                        redisSlackNameCache,
                        redisUserRoleCache
                ).run()
        );

        commandMap.put("/neworg", (userId, channelId, scoreText, responseUrl) ->
                () -> new CommandNeworg(
                        userId,
                        channelId,
                        scoreText,
                        responseUrl,
                        orgService,
                        userService,
                        redisUserRoleCache
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
        System.out.println("text: "+ scoreText);

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