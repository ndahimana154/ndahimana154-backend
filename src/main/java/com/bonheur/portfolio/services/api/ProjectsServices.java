package com.bonheur.portfolio.services.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;

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

        return Map.of("message", "Project created successfully", "action", 1, "data",
                Map.of("data", newProject));

    }
}
