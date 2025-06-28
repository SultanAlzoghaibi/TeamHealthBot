package com.teamheath.bot.Commands.Users.User;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Team.TeamDB;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserDB, UUID> {
    Optional<UserDB> findBySlackUserId(String slackUserId);

    Optional<Object> findWithTeamBySlackUserId(String userId);

    int countByTeam(TeamDB team);

    Optional<UserDB> findBySlackUserIdAndOrganization(String userId, OrgDB org);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserDB u WHERE u.slackUserId = :userId AND u.organization = :org")
    void deleteBySlackUserIdAndOrganization(@Param("userId") String userId, @Param("org") OrgDB org);

    List<UserDB> findByTeam(TeamDB team);
}