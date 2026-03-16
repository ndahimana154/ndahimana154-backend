package com.bonheur.portfolio.services.api;

import java.util.Map;
import java.util.Objects;
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
import com.bonheur.portfolio.dto.requests.UpdateProjectDto;
import com.bonheur.portfolio.models.Project;
import com.bonheur.portfolio.repositories.ProjectRepository;
import com.bonheur.portfolio.services.FileUploadUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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

        if (dto.getImages() == null || dto.getImages().length == 0) {
            return Map.of("message", "At least one image file is required", "action", 0);
        }

        String prefix = dto.getTitle() != null ? dto.getTitle() : "project";
        List<String> storedImagePaths = uploadFiles(dto.getImages(), "projects", prefix);

        Project project = Project.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .images(String.join(",", storedImagePaths))
                .category(dto.getCategory())
                .technologies(dto.getTechnologies()).url(dto.getUrl()).startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .build();

        Project newProject = projectRepository.save(project);
        newProject.setImages(mapToUrlCsv(newProject.getImages()));

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
    public Map<String, Object> updateProject(UUID id, UpdateProjectDto dto) {
        Objects.requireNonNull(id, "ID must not be null");
        Objects.requireNonNull(dto, "Update payload must not be null");

        Optional<Project> existingProject = projectRepository.findById(id);

        if (existingProject.isEmpty()) {
            return Map.of("message", "Project not found", "action", 0);
        }

        // System.out.println("UU");

        Project project = existingProject.get();

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            project.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }

        if (dto.getImages() != null && dto.getImages().length > 0) {
            String namePrefix = (dto.getTitle() != null && !dto.getTitle().isBlank()) ? dto.getTitle()
                    : project.getTitle();
            List<String> newImagePaths = uploadFiles(dto.getImages(), "projects", namePrefix);
            project.setImages(String.join(",", newImagePaths));
        }

        if (dto.getCategory() != null) {
            project.setCategory(dto.getCategory());
        }
        if (dto.getTechnologies() != null) {
            project.setTechnologies(dto.getTechnologies());
        }
        if (dto.getUrl() != null) {
            project.setUrl(dto.getUrl());
        }
        if (dto.getStartTime() != null) {
            project.setStartTime(dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            project.setEndTime(dto.getEndTime());
        }

        if (dto.getIsDeleted() != null) {
            project.setDeleted(dto.getIsDeleted());
        }

        Project updated = projectRepository.save(project);
        updated.setImages(mapToUrlCsv(updated.getImages()));

        return Map.of("message", "Project updated successfully", "action", 1, "data", Map.of("project", updated));
    }

    private List<String> uploadFiles(MultipartFile[] files, String subfolder, String namePrefix) {
        List<String> storedPaths = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String path = fileUploadUtil.uploadFile(file, subfolder, FileUploadUtil.DEFAULT_IMAGE_EXTENSIONS,
                    namePrefix);
            storedPaths.add(path);
        }
        return storedPaths;
    }

    private String mapToUrlCsv(String imagePathsCsv) {
        if (imagePathsCsv == null || imagePathsCsv.isBlank()) {
            return "";
        }
        return Arrays.stream(imagePathsCsv.split(","))
                .map(String::trim)
                .filter(it -> !it.isBlank())
                .map(fileUploadUtil::getFileUrl)
                .filter(url -> url != null && !url.isBlank())
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }
}
