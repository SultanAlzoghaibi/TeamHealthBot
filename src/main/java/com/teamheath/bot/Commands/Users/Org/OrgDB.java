package com.teamheath.bot.Commands.Users.Org;

import com.teamheath.bot.Commands.Users.User.UserDB;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "organizations")
public class OrgDB {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "slack_team_id", unique = true, nullable = false)
    private String slackTeamId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserDB> users;

    // Getters and Setters
    public List<UserDB> getUsers() {
        return users;
    }

    public void setUsers(List<UserDB> users) {
        this.users = users;
    }
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlackTeamId() {
        return slackTeamId;
    }

    public void setSlackTeamId(String slackTeamId) {
        this.slackTeamId = slackTeamId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public OrgDB orElse(Object o) {
        return (OrgDB) o;
    }
}