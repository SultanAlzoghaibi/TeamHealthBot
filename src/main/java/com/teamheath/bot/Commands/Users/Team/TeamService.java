package com.teamheath.bot.Commands.Users.Team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public TeamDB saveTeam(TeamDB team) {
        return teamRepository.save(team);
    }

    public List<TeamDB> getTeamsByOrganizationId(UUID orgId) {
        return teamRepository.findByOrganizationId(orgId);
    }

    public Optional<TeamDB> getTeamByNameAndOrg(String name, UUID orgId) {
        return teamRepository.findByNameAndOrganizationId(name, orgId);
    }

    public List<TeamDB> getAllTeams() {
        return teamRepository.findAll();
    }
}