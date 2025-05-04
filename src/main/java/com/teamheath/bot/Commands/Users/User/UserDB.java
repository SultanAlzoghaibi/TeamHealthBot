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

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private OrgDB organization;

    @CreationTimestamp
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "team_id")  // optional: nullable = true if user might not be in a team
    private TeamDB team;


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


}

