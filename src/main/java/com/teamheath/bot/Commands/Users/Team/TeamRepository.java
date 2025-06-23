package com.teamheath.bot.Commands.Users.Team;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<TeamDB, UUID> {
    List<TeamDB> findByOrganizationId(UUID organizationId);
    Optional<TeamDB> findByNameAndOrganizationId(String name, UUID organizationId);

    List<TeamDB> findByOrganization(OrgDB org);

    // In TeamRepository.java
    @Query("SELECT t FROM TeamDB t WHERE t.organization = :org")
    List<TeamDB> findTeamsByOrganization(@Param("org") OrgDB org);

    Optional<TeamDB> findByNameAndOrganization(String teamName, OrgDB org);
}