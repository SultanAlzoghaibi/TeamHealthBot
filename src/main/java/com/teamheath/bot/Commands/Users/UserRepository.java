package com.teamheath.bot.Commands.Users;

import com.teamheath.bot.Commands.Users.UserDB;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserDB, UUID> {
}