package com.teamheath.bot.Commands.Users.UserScore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserScoreService {

    @Autowired
    private UserScoreRepository userScoreRepository;

    @Transactional
    public UserScoreDB saveScore(UserScoreDB score) {
        return userScoreRepository.save(score);
    }

    @Transactional(readOnly = true)
    public List<UserScoreDB> getScoresByUserId(UUID userId) {
        return userScoreRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<UserScoreDB> getScoresByTeamId(UUID teamId) {
        return userScoreRepository.findByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public List<UserScoreDB> getRecentScores(UUID userId) {
        return userScoreRepository.findByUserIdOrderByRecordedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<UserScoreDB> getAllScores() {
        return userScoreRepository.findAll();
    }
}