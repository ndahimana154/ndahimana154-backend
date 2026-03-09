package com.bonheur.portfolio.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonheur.portfolio.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
