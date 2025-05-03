package com.teamheath.bot.Commands.Users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrgRepository extends JpaRepository<OrgDB, UUID> {
    OrgDB findBySlackTeamId(String slackTeamId); // Optional custom query
}