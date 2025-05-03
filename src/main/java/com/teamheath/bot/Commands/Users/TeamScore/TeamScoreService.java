package com.teamheath.bot.Commands.Users.TeamScore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TeamScoreService {

    @Autowired
    private TeamScoreRepository teamScoreRepository;

    public TeamScoreDB saveScore(TeamScoreDB score) {
        return teamScoreRepository.save(score);
    }

    public List<TeamScoreDB> getScoresByTeamId(UUID teamId) {
        return teamScoreRepository.findByTeamId(teamId);
    }

    public List<TeamScoreDB> getAllScores() {
        return teamScoreRepository.findAll();
    }
}