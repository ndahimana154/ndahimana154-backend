package com.bonheur.portfolio.controllers.remote;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("remoteProjectsController")
@RequestMapping("/remote/projects")
public class ProjectsController {

    @GetMapping("/")
    public String getAllProjects() {
        return "List of all projects for frontoffice";
    }

    @GetMapping("/{id}")
    public String getProjectById(String id) {
        return "Project details for ID: " + id;
    }

}
