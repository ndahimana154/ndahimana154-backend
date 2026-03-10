package com.bonheur.portfolio.services.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonheur.portfolio.dto.requests.CreateProjectsDto;
import com.bonheur.portfolio.models.Project;
import com.bonheur.portfolio.repositories.ProjectRepository;

@Service
public class ProjectsServices {
    private final ProjectRepository projectRepository;

    public ProjectsServices(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Map<String, Object> createProject(CreateProjectsDto dto) {
        Optional<Project> existingProject = projectRepository.findByTitle(dto.getTitle());
        if (existingProject.isPresent()) {
            return Map.of("message", "Project already exists", "action", 0);
        }

        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setImages(dto.getImages());
        project.setCategory(dto.getCategory());
        project.setTechnologies(dto.getTechnologies());
        project.setUrl(dto.getUrl());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            project.setStartTime(dateFormat.parse(dto.getStartTime()));
            project.setEndTime(dateFormat.parse(dto.getEndTime()));
        } catch (ParseException e) {
            return Map.of("message", "Invalid date format", "action", 0);
        }

        Project newProject = projectRepository.save(project);
        return Map.of("message", "Project created successfully", "action", 1, "data", Map.of("data", newProject));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAllProjects() {
        java.util.List<Project> projects = projectRepository.findAll();
        return Map.of("message", "Projects retrieved successfully", "action", 1, "data", Map.of("projects", projects));
    }

    @Transactional(readOnly = true)
    public Optional<Project> getProjectById(UUID id) {
        return projectRepository.findById(id);
    }

    @Transactional
    public Map<String, Object> updateProject(UUID id, CreateProjectsDto dto) {
        Optional<Project> existingProject = projectRepository.findById(id);
        if (existingProject.isEmpty()) {
            return Map.of("message", "Project not found", "action", 0);
        }

        Project project = existingProject.get();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setImages(dto.getImages());
        project.setCategory(dto.getCategory());
        project.setTechnologies(dto.getTechnologies());
        project.setUrl(dto.getUrl());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            project.setStartTime(dateFormat.parse(dto.getStartTime()));
            project.setEndTime(dto.getEndTime() != null ? dateFormat.parse(dto.getEndTime()) : null);
        } catch (ParseException e) {
            return Map.of("message", "Invalid date format", "action", 0);
        }

        Project updated = projectRepository.save(project);
        return Map.of("message", "Project updated successfully", "action", 1, "data", Map.of("data", updated));
    }

    @Transactional
    public Map<String, Object> deleteProject(UUID id) {
        Optional<Project> existingProject = projectRepository.findById(id);
        if (existingProject.isEmpty()) {
            return Map.of("message", "Project not found", "action", 0);
        }

        projectRepository.deleteById(id);
        return Map.of("message", "Project deleted successfully", "action", 1);
    }
}
