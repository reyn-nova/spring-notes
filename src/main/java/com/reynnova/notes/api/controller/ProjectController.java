package com.reynnova.notes.api.controller;

import org.hibernate.Hibernate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;

import java.util.List;

import com.reynnova.notes.api.model.Project;
import com.reynnova.notes.service.SessionFactoryProvider;

@RestController
public class ProjectController {

    @GetMapping(value={"/project", "/project/"}, params = "!id")
    public List<Project> getProjects() {
        Session session = SessionFactoryProvider.establishSession();

        CriteriaQuery<Project> criteria = session.getCriteriaBuilder().createQuery(Project.class);
        criteria.from(Project.class);

        List<Project> list = session.createQuery(criteria).getResultList();

        for (Project item : list) {
            item.setNotes(null);
        }

        session.close();

        return list;
    }


    @GetMapping(value = "/project", params = "id")
    public Project getProject(@RequestParam int id) {
        Session session = SessionFactoryProvider.establishSession();

        Project project = session.get(Project.class, id);
        Hibernate.initialize(project.getNotes());

        session.close();

        return project;
    }
}
