package com.teamheath.bot.Commands.Users.User;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreDB;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.teamheath.bot.tools.Response3SecMore.response3SecMore;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private UserScoreRepository userScoreRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(UserDB user) {
        userRepository.save(user);
    }

    @Transactional
    public UserDB findBySlackUserId(String userId) {
        return (UserDB) userRepository.findWithTeamBySlackUserId(userId).orElse(null);
    }
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Query("SELECT u FROM UserDB u LEFT JOIN FETCH u.team WHERE u.slackUserId = :userId")
    Optional<UserDB> findWithTeamBySlackUserId(@Param("userId") String userId) {
        return null;
    }


    public UserScoreDB saveScore(UserScoreDB score) {
        return userScoreRepository.save(score);
    }

    public UserDB findBySlackId(String userId) {
        return (UserDB) userRepository.findWithTeamBySlackUserId(userId).orElse(null);
    }

    public void createUser(String userId, OrgDB newOrg, String role) {

        UserDB newUser = new UserDB();
        newUser.setSlackUserId(userId);
        newUser.setOrganization(newOrg);
        newUser.setRole(role); // 👑 Set admin role
        userRepository.save(newUser);
    }

    @Transactional
    public void removeUserFromOrg(String userId, OrgDB org) {
        try {Optional<UserDB> userOpt = userRepository.findBySlackUserIdAndOrgId(userId, org.getId());

            if (userOpt.isEmpty()) {
                System.out.println("❗ User not found for given org and ID");
                return;
            }
            UserDB user = userOpt.get();
            // Either delete or disassociate
            user.setOrganization(null);
            user.setRole(null);
            userRepository.save(user); // or userRepository.delete(user);
            System.out.println("✅ Removed user " + userId + " from org " + org.getName());

        } catch (Exception e) {
            System.out.println("❗ Error while removing user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Optional<UserDB> findBySlackUserIdAndOrganization(String userId, OrgDB org) {
        return userRepository.findBySlackUserIdAndOrganization(userId, org);
    }
}