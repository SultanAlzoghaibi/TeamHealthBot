package com.teamheath.bot.Commands.Users.UserScore;

import com.teamheath.bot.Commands.Users.Team.TeamDB;
import com.teamheath.bot.Commands.Users.User.UserDB;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_scores")
public class UserScoreDB {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserDB user;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private TeamDB team;

    @Column(nullable = false)
    private int score;

    @CreationTimestamp
    @Column(name = "recorded_at", nullable = false, updatable = false)
    private Instant recordedAt;

    // Getters & Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserDB getUser() {
        return user;
    }

    public void setUser(UserDB user) {
        this.user = user;
    }

    public TeamDB getTeam() {
        return team;
    }

    public void setTeam(TeamDB team) {
        this.team = team;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }
}