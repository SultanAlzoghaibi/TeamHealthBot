package com.teamheath.bot.Commands.Users.Team;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreDB;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreRepository;
import com.teamheath.bot.Commands.Users.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TeamService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamScoreRepository teamScoreRepository;

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

    public List<TeamWithCountDTO> getTeamsWithMemberCountByOrganization(OrgDB org) {
        List<TeamDB> teams = teamRepository.findByOrganization(org);

        List<TeamWithCountDTO> result = new ArrayList<>();
        for (TeamDB team : teams) {

            int count = userRepository.countByTeam(team);
            result.add(new TeamWithCountDTO(team.getName(), count, team));
        }
        return result;
    }



    public TeamScoreDB saveTeamScore(TeamScoreDB score) {
        return teamScoreRepository.save(score);
    }

    public Optional<TeamDB> findByNameAndOrganization(String teamName, OrgDB org) {
        return teamRepository.findByNameAndOrganization(teamName, org);
    }
}

