package com.teamheath.bot.Commands.Users.TeamScore;

import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamScoreRepository extends JpaRepository<TeamScoreDB, UUID> {
    List<TeamScoreDB> findByTeamId(UUID teamId);
}