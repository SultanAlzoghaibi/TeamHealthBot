package com.teamheath.bot.Commands.Users.UserScore;

import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.UserScore.UserScoreDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserScoreRepository extends JpaRepository<UserScoreDB, UUID> {

    List<UserScoreDB> findByUserId(UUID userId);

    List<UserScoreDB> findByTeamId(UUID teamId);

    List<UserScoreDB> findByUserIdOrderByRecordedAtDesc(UUID userId);

    Optional<UserScoreDB> findTopByUserOrderByRecordedAtDesc(UserDB user);
}