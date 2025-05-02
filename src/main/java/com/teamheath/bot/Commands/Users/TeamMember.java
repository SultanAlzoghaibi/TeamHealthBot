package com.teamheath.bot.Commands.Users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TeamMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String role;
    private Integer teamId;
    private String Rank;

    public TeamMember(String userId, String role, Integer teamId, String Rank) {
        this.userId = userId;
        this.role = role;
        this.teamId = teamId;
        this.Rank = Rank;
    }

    public String getRank() {return Rank;
    }
    public void setRank(String rank) {Rank = rank;
    }
    public Integer getTeamId() {return teamId;
    }
    public void setTeamId(Integer teamId) {this.teamId = teamId;
    }
    public String getRole() {return role;
    }
    public void setRole(String role) {this.role = role;
    }
    public String getUserId() {return userId;
    }
    public void setUserId(String userId) {this.userId = userId;
    }
    public Long getId() {return id;
    }

    public void setId(Long id) {this.id = id;}

    public TeamMember() {
    }




    // Getters & Setters
}

