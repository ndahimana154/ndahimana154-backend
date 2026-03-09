package com.bonheur.portfolio.controllers.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bonheur.portfolio.dto.requests.CreateProjectsDto;
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

        if (validationResponse != null) {
            return validationResponse;
        }

        Map<String, Object> result = projectsService.createProject(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/")
    public String getAllProjects() {
        return "List of all projects for backoffice";
    }

    @PutMapping("/{id}")
    public String updateProject(String id, String projectData) {
        return "Project " + id + " updated: " + projectData;
    }

    @DeleteMapping("/{id}")
    public String deleteProject(String id) {
        return "Project " + id + " deleted";
    }

}
