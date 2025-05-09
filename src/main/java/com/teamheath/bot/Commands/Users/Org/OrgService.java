package com.teamheath.bot.Commands.Users.Org;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void deleteAll() {
        orgRepository.deleteAll();
    }
}