package com.teamheath.bot.Commands.Users;

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
}