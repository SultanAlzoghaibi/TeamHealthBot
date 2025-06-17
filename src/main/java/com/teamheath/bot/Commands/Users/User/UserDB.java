package com.teamheath.bot.Commands.Users.User;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.Team.TeamDB;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")

public class UserDB {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String slackUserId;

    @Column(nullable = false)
    private String role; // e.g., "ADMIN", "USER", "PM"

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private OrgDB organization;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private TeamDB team;

    @CreationTimestamp
    private Instant createdAt;


    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public String getSlackUserId() {
        return slackUserId;
    }

    public void setSlackUserId(String slackUserId) {
        this.slackUserId = slackUserId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public OrgDB getOrganization() {
        return organization;
    }

    public void setOrganization(OrgDB organization) {
        this.organization = organization;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UserDB orElse(Object o) {
        return (UserDB) o;
    }


    public TeamDB getTeam() { return team;};
    public void setTeam(TeamDB team) {
        this.team = team;

    }

    public boolean isEmpty() {
        return slackUserId == null;
    }


    public UserDB get() {
        return this;
    }
}

