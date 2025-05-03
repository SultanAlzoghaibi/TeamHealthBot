package com.teamheath.bot.Commands.Users;

import com.teamheath.bot.Commands.Users.OrgRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orgs")
public class OrgController {

    private final OrgRepository orgRepository;

    public OrgController(OrgRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    @GetMapping
    public List<OrgDB> getAllOrganizations() {
        return orgRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrgDB> getOrgById(@PathVariable UUID id) {
        return orgRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


}