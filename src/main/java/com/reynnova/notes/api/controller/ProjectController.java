package com.reynnova.notes.api.controller;

import com.reynnova.notes.api.model.Project;
import com.reynnova.notes.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ProjectController {

    private ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping(value={"/project", "/project/"}, params = "!id")
    public List<Project> getProjects() {
        return projectService.getProjects();
    }

    @GetMapping(value = "/project", params = "id")
    public Project getProject(@RequestParam int id) {
        Optional<Project> project = projectService.getProject(id);

        if (project.isPresent()) {
            return (Project) project.get();
        }

        return null;
    }
}
