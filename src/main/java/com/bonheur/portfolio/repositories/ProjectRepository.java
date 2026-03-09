package com.bonheur.portfolio.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bonheur.portfolio.models.Project;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Optional<Project> findByTitle(String title);
}
