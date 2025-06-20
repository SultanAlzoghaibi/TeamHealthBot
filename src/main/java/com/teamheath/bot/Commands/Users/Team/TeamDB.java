package com.teamheath.bot.Commands.Users.Team;

import com.teamheath.bot.Commands.Users.Org.OrgDB;
import com.teamheath.bot.Commands.Users.TeamScore.TeamScoreDB;
import com.teamheath.bot.Commands.Users.User.UserDB;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.OrderBy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "teams")
public class TeamDB {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserDB> users;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrgDB organization;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @jakarta.persistence.OrderBy("recordedAt DESC")
    private List<TeamScoreDB> teamScores;


    // Getters & Setters

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

    public OrgDB getOrganization() {
        return organization;
    }

    public void setOrganization(OrgDB organization) {
        this.organization = organization;
    }

    public List<UserDB> getUsers() {
        return users;
    }

    public void setUsers(List<UserDB> users) {
        this.users = users;
    }

    public List<TeamScoreDB> getTeamScores() {
        return teamScores;
    }

    public void setTeamScores(List<TeamScoreDB> teamScores) {
        this.teamScores = teamScores;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}