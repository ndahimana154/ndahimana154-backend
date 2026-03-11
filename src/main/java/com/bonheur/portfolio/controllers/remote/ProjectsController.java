package com.bonheur.portfolio.controllers.remote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bonheur.portfolio.models.Project;
import com.bonheur.portfolio.services.FileUploadUtil;
import com.bonheur.portfolio.services.api.ProjectsServices;

@RestController("remoteProjectsController")
@RequestMapping("/remote/projects")
public class ProjectsController {

    private final ProjectsServices projectsService;
    private final FileUploadUtil fileUploadUtil;

    public ProjectsController(ProjectsServices projectsService, FileUploadUtil fileUploadUtil) {
        this.projectsService = projectsService;
        this.fileUploadUtil = fileUploadUtil;
    }

    @GetMapping("/")
    public ResponseEntity<java.util.Map<String, Object>> getAllProjects() {
        var result = projectsService.getAllProjects();

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

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        return projectsService.getProjectById(id)
                .map(project -> {
                    project.setImages(fileUploadUtil.getFileUrl(project.getImages()));
                    return ResponseEntity.ok(project);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void decorateWithFileUrl(List<Project> projects) {
        for (Project project : projects) {
            project.setImages(fileUploadUtil.getFileUrl(project.getImages()));
        }
    }
}
