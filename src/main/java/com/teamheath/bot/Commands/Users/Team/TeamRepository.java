package com.teamheath.bot.Commands.Users.Team;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<TeamDB, UUID> {
    List<TeamDB> findByOrganizationId(UUID organizationId);
    Optional<TeamDB> findByNameAndOrganizationId(String name, UUID organizationId);

    List<TeamDB> findByOrganization(OrgDB org);
}