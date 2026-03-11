package com.bonheur.portfolio.services.api;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bonheur.portfolio.dto.requests.CreateProjectsDto;
import com.bonheur.portfolio.models.Project;
import com.bonheur.portfolio.repositories.ProjectRepository;
import com.bonheur.portfolio.services.FileUploadUtil;

@Service
public class ProjectsServices {
    private final ProjectRepository projectRepository;
    private final FileUploadUtil fileUploadUtil;

    public ProjectsServices(ProjectRepository projectRepository, FileUploadUtil fileUploadUtil) {
        this.projectRepository = projectRepository;
        this.fileUploadUtil = fileUploadUtil;
    }

    @Transactional
    public Map<String, Object> createProject(CreateProjectsDto dto) {
        Optional<Project> existingProject = projectRepository.findByTitle(dto.getTitle());
        if (existingProject.isPresent()) {
            return Map.of("message", "Project already exists", "action", 0);
        }

        Project project = new Project();

        if (dto.getImages() == null || dto.getImages().isEmpty()) {
            return Map.of("message", "Image file is required", "action", 0);
        }

        String prefix = dto.getTitle() != null ? dto.getTitle() : "project";
        String storedImageFilename = fileUploadUtil.uploadFile(dto.getImages(), "projects",
                FileUploadUtil.DEFAULT_IMAGE_EXTENSIONS, prefix);

        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setImages(storedImageFilename);
        project.setCategory(dto.getCategory());
        project.setTechnologies(dto.getTechnologies());
        project.setUrl(dto.getUrl());
        project.setStartTime(dto.getStartTime());
        project.setEndTime(dto.getEndTime());

        Project newProject = projectRepository.save(project);
        newProject.setImages(fileUploadUtil.getFileUrl(newProject.getImages()));

        return Map.of("message", "Project created successfully", "action", 1, "data", Map.of("project", newProject));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAllProjects() {
        java.util.List<Project> projects = projectRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        return Map.of("message", "Projects retrieved successfully", "action", 1, "data", Map.of("projects", projects));
    }

    @Transactional(readOnly = true)
    public Page<Project> searchProjects(String title, String category, String technologies, int page, int size,
            String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.fromString(direction.toUpperCase()), sortBy));

        Specification<Project> spec = (root, query, cb) -> {
            jakarta.persistence.criteria.Predicate p = cb.conjunction();
            if (title != null && !title.isBlank()) {
                p = cb.and(p, cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (category != null && !category.isBlank()) {
                p = cb.and(p, cb.like(cb.lower(root.get("category")), "%" + category.toLowerCase() + "%"));
            }
            if (technologies != null && !technologies.isBlank()) {
                p = cb.and(p, cb.like(cb.lower(root.get("technologies")), "%" + technologies.toLowerCase() + "%"));
            }
            return p;
        };

        return projectRepository.findAll(spec, pageable);
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

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            String namePrefix = dto.getTitle() != null ? dto.getTitle() : "project";
            String newImageFilename = fileUploadUtil.uploadFile(dto.getImages(), "projects",
                    FileUploadUtil.DEFAULT_IMAGE_EXTENSIONS, namePrefix);
            project.setImages(newImageFilename);
        }

        project.setCategory(dto.getCategory());
        project.setTechnologies(dto.getTechnologies());
        project.setUrl(dto.getUrl());
        project.setStartTime(dto.getStartTime());
        project.setEndTime(dto.getEndTime());

        Project updated = projectRepository.save(project);
        updated.setImages(fileUploadUtil.getFileUrl(updated.getImages()));

        return Map.of("message", "Project updated successfully", "action", 1, "data", Map.of("project", updated));
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
