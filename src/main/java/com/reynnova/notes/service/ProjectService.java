package com.reynnova.notes.service;

import com.reynnova.notes.api.model.Project;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private List<Project> projectList;

    public ProjectService() {
        projectList = new ArrayList<>();

        Project project1 = new Project(1, "Project Pertama");
        Project project2 = new Project(2, "Project Kedua");
        Project project3 = new Project(3, "Project Ketiga");
        Project project4 = new Project(4, "Project Keempat");
        Project project5 = new Project(5, "Project Kelima");

        projectList.addAll(Arrays.asList(project1, project2, project3, project4, project5));
    }

    public Optional<Project> getProject(Integer id) {
        Optional optional = Optional.empty();

        for (Project project: projectList) {
            if (id == project.getId()) {
                optional = Optional.of(project);

                break;
            }
        }

        return optional;
    }

    public List<Project> getProjects() {
        return projectList;
    }
}
