package com.bonheur.portfolio.controllers.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bonheur.portfolio.dto.requests.CreateProjectsDto;
import com.bonheur.portfolio.dto.requests.UpdateProjectDto;
import com.bonheur.portfolio.models.Project;
import com.bonheur.portfolio.services.FileUploadUtil;
import com.bonheur.portfolio.services.api.ProjectsServices;
import com.bonheur.portfolio.utils.ValidationUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController("apiProjectsController")
@RequestMapping("/backoffice/projects")
@Tag(name = "Back-office Projects endpoints", description = "Endpoints for managing projects for the back office")
public class ProjectsController {
    private final ValidationUtils validationUtils;
    private final ProjectsServices projectsService;
    private final FileUploadUtil fileUploadUtil;

    public ProjectsController(ValidationUtils validationUtils, ProjectsServices projectsService,
            FileUploadUtil fileUploadUtil) {
        this.validationUtils = validationUtils;
        this.projectsService = projectsService;
        this.fileUploadUtil = fileUploadUtil;
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new project", description = "Creates a new project with the provided details and images.")
    public ResponseEntity<Map<String, Object>> createProject(@ModelAttribute CreateProjectsDto dto) {
        ResponseEntity<Map<String, Object>> validationResponse = validationUtils.validateAndBuildResponse(dto);

        if (validationResponse != null) {
            return validationResponse;
        }

        Map<String, Object> result = projectsService.createProject(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/")
    @Operation(summary = "Get all projects", description = "Retrieves a list of all projects.")
    public ResponseEntity<Map<String, Object>> getAllProjects() {
        Map<String, Object> result = projectsService.getAllProjects();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @Operation(summary = "Search projects", description = "Searches for projects based on title, category, and technologies with pagination and sorting options.")
    public ResponseEntity<Map<String, Object>> searchProjects(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String technologies,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        var pageResult = projectsService.searchProjects(title, category, technologies, page, size, sortBy, direction);
        List<Project> content = pageResult.getContent();
        decorateWithFileUrl(content);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Projects retrieved successfully");
        response.put("action", 1);
        response.put("data", Map.of(
                "projects", content,
                "currentPage", pageResult.getNumber(),
                "pageSize", pageResult.getSize(),
                "totalItems", pageResult.getTotalElements(),
                "totalPages", pageResult.getTotalPages()));
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Update a project", description = "Updates an existing project with the provided details and images.")
    public ResponseEntity<Map<String, Object>> updateProject(@PathVariable UUID id,
            @ModelAttribute UpdateProjectDto dto) {
        ResponseEntity<Map<String, Object>> validationResponse = validationUtils.validateAndBuildResponse(dto);
        if (validationResponse != null) {
            return validationResponse;
        }

        System.out.println("Received request to update project with ID: " + id);

        Map<String, Object> result = projectsService.updateProject(id, dto);
        return ResponseEntity.ok(result);
    }

    private void decorateWithFileUrl(List<Project> projects) {
        for (Project project : projects) {
            String images = project.getImages();
            if (images == null || images.isBlank()) {
                continue;
            }
            List<String> finalUrls = Arrays.stream(images.split(","))
                    .map(String::trim)
                    .filter(it -> !it.isBlank())
                    .map(it -> it.startsWith("http") ? it : fileUploadUtil.getFileUrl(it))
                    .toList();
            project.setImages(String.join(",", finalUrls));
        }
    }
}
