package com.teamheath.bot.Commands.Users.Team;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreDB;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreRepository;
import com.teamheath.bot.Commands.Users.User.UserDB;
import com.teamheath.bot.Commands.Users.User.UserRepository;
import jakarta.transaction.Transactional;
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

    public TeamDB createTeam(String teamName, OrgDB org) {
        TeamDB team = new TeamDB();
        team.setName(teamName);
        team.setOrganization(org);
        return teamRepository.save(team);

    }

    public List<TeamDB> findAllTeams() {
        return teamRepository.findAll();
    }

    @Transactional
    public void deleteTeam(TeamDB team) {
        // Optional: remove users from this team first if needed
        List<UserDB> users = userRepository.findByTeam(team);
        for (UserDB user : users) {
            user.setTeam(null);
        }
        userRepository.saveAll(users);

        teamRepository.delete(team);
    }
}

