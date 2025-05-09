package com.teamheath.bot.Commands.Users.User;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(UserDB user) {
        userRepository.save(user);
    }

    @Transactional
    public UserDB findBySlackUserId(String userId) {
        return userRepository.findBySlackUserId(userId).orElse(null);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }
}