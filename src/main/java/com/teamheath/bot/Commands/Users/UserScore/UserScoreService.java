package com.teamheath.bot.Commands.Users.UserScore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserScoreService {

    @Autowired
    private UserScoreRepository userScoreRepository;

    public UserScoreDB saveScore(UserScoreDB score) {
        return userScoreRepository.save(score);
    }

    public List<UserScoreDB> getScoresByUserId(UUID userId) {
        return userScoreRepository.findByUserId(userId);
    }

    public List<UserScoreDB> getScoresByTeamId(UUID teamId) {
        return userScoreRepository.findByTeamId(teamId);
    }

    public List<UserScoreDB> getRecentScores(UUID userId) {
        return userScoreRepository.findByUserIdOrderByRecordedAtDesc(userId);
    }

    public List<UserScoreDB> getAllScores() {
        return userScoreRepository.findAll();
    }
}