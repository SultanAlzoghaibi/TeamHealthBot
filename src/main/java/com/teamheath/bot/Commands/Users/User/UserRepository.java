package com.teamheath.bot.Commands.Users.User;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Team.TeamDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserDB, UUID> {
    Optional<UserDB> findBySlackUserId(String slackUserId);

    Optional<Object> findWithTeamBySlackUserId(String userId);

    int countByTeam(TeamDB team);

    Optional<UserDB> findBySlackUserIdAndOrganization(String userId, OrgDB org);
}