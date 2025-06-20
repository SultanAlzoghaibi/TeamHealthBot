package com.teamheath.bot.Commands.Users.TeamScore;

import com.teamheath.bot.Commands.Users.Team.TeamDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public List<Integer> getLastNScoresForTeam(TeamDB team, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return teamScoreRepository.findTopNScoresByTeamOrderByRecordedAtDesc(team, pageable)
                .stream()
                .map(TeamScoreDB::getScore)
                .toList();
    }

    public void saveTeamScore(TeamScoreDB teamScore) {
        teamScoreRepository.save(teamScore);

    }
}