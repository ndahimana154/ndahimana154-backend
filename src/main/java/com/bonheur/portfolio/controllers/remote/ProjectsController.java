package com.bonheur.portfolio.controllers.remote;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bonheur.portfolio.models.Project;
import com.bonheur.portfolio.services.api.ProjectsServices;

@RestController("remoteProjectsController")
@RequestMapping("/remote/projects")
public class ProjectsController {

    private final ProjectsServices projectsService;

    public ProjectsController(ProjectsServices projectsService) {
        this.projectsService = projectsService;
    }

    @GetMapping("/")
    public ResponseEntity<java.util.Map<String, Object>> getAllProjects() {
        return ResponseEntity.ok(projectsService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        return projectsService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
