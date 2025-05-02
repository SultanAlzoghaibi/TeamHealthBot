package com.teamheath.bot.Commands.Users;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;

@Entity
public class ScoresData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @ElementCollection
    private List<Integer> weeklyScores; // e.g., [83, 76, 92]

    public ScoresData() {}

    public ScoresData(String userId, List<Integer> weeklyScores) {
        this.userId = userId;
        this.weeklyScores = weeklyScores;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Integer> getWeeklyScores() {
        return weeklyScores;
    }

    public void setWeeklyScores(List<Integer> weeklyScores) {
        this.weeklyScores = weeklyScores;
    }
}