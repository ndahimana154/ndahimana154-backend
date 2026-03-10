package com.bonheur.portfolio.controllers.api;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bonheur.portfolio.dto.requests.CreateProjectsDto;
import com.bonheur.portfolio.models.Project;
import com.bonheur.portfolio.services.api.ProjectsServices;
import com.bonheur.portfolio.utils.ValidationUtils;

@RestController("apiProjectsController")
@RequestMapping("/backoffice/projects")
public class ProjectsController {
    private final ValidationUtils validationUtils;
    private final ProjectsServices projectsService;

    public ProjectsController(ValidationUtils validationUtils, ProjectsServices projectsService) {
        this.validationUtils = validationUtils;
        this.projectsService = projectsService;
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> createProject(@RequestBody CreateProjectsDto dto) {
        ResponseEntity<Map<String, Object>> validationResponse = validationUtils.validateAndBuildResponse(dto);
        System.out.println("Validation response: " + validationResponse);
        if (validationResponse != null) {
            return validationResponse;
        }

        Map<String, Object> result = projectsService.createProject(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> getAllProjects() {
        Map<String, Object> result = projectsService.getAllProjects();

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProject(@PathVariable UUID id,
            @RequestBody CreateProjectsDto dto) {
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
}
