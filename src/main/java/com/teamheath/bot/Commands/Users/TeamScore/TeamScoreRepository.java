package com.teamheath.bot.Commands.Users.TeamScore;

import com.teamheath.bot.Commands.Users.Team.TeamDB;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreDB;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface TeamScoreRepository extends JpaRepository<TeamScoreDB, UUID> {
    List<TeamScoreDB> findByTeamId(UUID teamId);


    @Query("SELECT s FROM TeamScoreDB s WHERE s.team = :team ORDER BY s.recordedAt DESC")
    List<TeamScoreDB> findTopNScoresByTeamOrderByRecordedAtDesc(TeamDB team, org.springframework.data.domain.Pageable pageable);}