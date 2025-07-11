package com.teamheath.bot.Commands.Users.Org;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrgService {
    @Autowired
    private OrgRepository orgRepository;

    public List<OrgDB> getAllOrganizations() {
        return orgRepository.findAll();
    }

    public OrgDB saveOrganization(OrgDB org) {
        return orgRepository.save(org);
    }

    public boolean existsBySlackTeamId(String t654321) {
        if (orgRepository.findBySlackTeamId(t654321) == null) {
            return false;
        }
        return true;
    }

    public OrgDB findBySlackTeamId(String t123456) {
        return orgRepository.findBySlackTeamId(t123456);
    }

    public Optional<OrgDB> findByName(String name) {
        return orgRepository.findByName(name);
    }

    public void deleteAll() {
        orgRepository.deleteAll();
    }


    public OrgDB createOrg(String orgName, String rawPassword, String slackTeamId) {
        OrgDB org = new OrgDB();
        org.setName(orgName);
        org.setSlackTeamId(slackTeamId);

        // TEMPORARY: just to unblock you for now
        org.setHashedPassword(rawPassword);

        // Later: use proper hashing
        // org.setHashedPassword(passwordEncoder.encode(rawPassword));

        return orgRepository.save(org);
    }


    public void deleteOrg(OrgDB org) {
        orgRepository.delete(org);
    }

    public List<OrgDB> findAll() {
        return orgRepository.findAll();
    }
}