package com.bonheur.portfolio.controllers.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bonheur.portfolio.dto.requests.CreateProjectsDto;
import com.bonheur.portfolio.models.Project;
import com.bonheur.portfolio.services.FileUploadUtil;
import com.bonheur.portfolio.services.api.ProjectsServices;
import com.bonheur.portfolio.utils.ValidationUtils;

@RestController("apiProjectsController")
@RequestMapping("/backoffice/projects")
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
    public ResponseEntity<Map<String, Object>> createProject(@ModelAttribute CreateProjectsDto dto) {
        ResponseEntity<Map<String, Object>> validationResponse = validationUtils.validateAndBuildResponse(dto);

        if (validationResponse != null) {
            return validationResponse;
        }

        Map<String, Object> result = projectsService.createProject(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getAllProjects() {
        System.out.println("Received request to get all projects");
        Map<String, Object> result = projectsService.getAllProjects();

        if (result.containsKey("data")) {
            var data = (Map<String, Object>) result.get("data");
            if (data != null && data.containsKey("projects")) {
                List<Project> projects = (List<Project>) data.get("projects");
                if (projects != null) {
                    decorateWithFileUrl(projects);
                    data.put("projects", projects);
                }
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
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
    public ResponseEntity<Map<String, Object>> updateProject(@PathVariable UUID id,
            @ModelAttribute CreateProjectsDto dto) {
        ResponseEntity<Map<String, Object>> validationResponse = validationUtils.validateAndBuildResponse(dto);
        if (validationResponse != null) {
            return validationResponse;
        }

        Map<String, Object> result = projectsService.updateProject(id, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProject(@PathVariable UUID id) {
        Map<String, Object> result = projectsService.deleteProject(id);
        return ResponseEntity.ok(result);
    }

    private void decorateWithFileUrl(List<Project> projects) {
        for (Project project : projects) {
            project.setImages(fileUploadUtil.getFileUrl(project.getImages()));
        }
    }
}
