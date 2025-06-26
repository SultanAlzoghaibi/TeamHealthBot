package com.teamheath.bot.Commands.Users.User;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Team.TeamDB;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserDB, UUID> {
    Optional<UserDB> findBySlackUserId(String slackUserId);

    Optional<Object> findWithTeamBySlackUserId(String userId);

    int countByTeam(TeamDB team);

    @Query("SELECT u FROM UserDB u WHERE u.slackUserId = :slackUserId AND u.organization.id = :orgId")
    Optional<UserDB> findBySlackUserIdAndOrgId(@Param("slackUserId") String slackUserId, @Param("orgId") UUID orgId);

    Optional<UserDB> findBySlackUserIdAndOrganization(String userId, OrgDB org);
}