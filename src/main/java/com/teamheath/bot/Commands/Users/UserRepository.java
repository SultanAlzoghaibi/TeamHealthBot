package com.teamheath.bot.Commands.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserDB, UUID> {
}