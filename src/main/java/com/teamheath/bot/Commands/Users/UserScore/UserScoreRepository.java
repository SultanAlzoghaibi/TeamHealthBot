package com.teamheath.bot.Commands.Users.UserScore;

import com.teamheath.bot.Commands.Users.UserScore.UserScoreDB;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface UserScoreRepository extends JpaRepository<UserScoreDB, UUID> {

    List<UserScoreDB> findByUserId(UUID userId);

    List<UserScoreDB> findByTeamId(UUID teamId);

    List<UserScoreDB> findByUserIdOrderByRecordedAtDesc(UUID userId);
}