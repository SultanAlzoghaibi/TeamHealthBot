package com.teamheath.bot.Commands.Users.UserScore;

import com.teamheath.bot.Commands.Users.User.UserDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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


    public Map<UUID, UserScoreDB> getRecentScoresForUsers(List<UserDB> users) {
        Map<UUID, UserScoreDB> result = new HashMap<>();
        for (UserDB user : users) {
            List<UserScoreDB> scores = userScoreRepository.findByUserIdOrderByRecordedAtDesc(user.getId());
            if (!scores.isEmpty()) {
                result.put(user.getId(), scores.get(0)); // latest score
            }
        }
        return result;
    }

    public UserScoreDB getMostRecentScore(UserDB matchedUser) {
        return userScoreRepository
                .findTopByUserOrderByRecordedAtDesc(matchedUser)
                .orElse(null);
    }
}