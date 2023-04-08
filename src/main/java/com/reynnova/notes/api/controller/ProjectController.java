package com.reynnova.notes.api.controller;

import com.reynnova.notes.api.model.Note;
import org.hibernate.Hibernate;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.reynnova.notes.api.model.Project;
import com.reynnova.notes.service.SessionFactoryProvider;

@RestController
public class ProjectController {

    @GetMapping(value={"/project", "/project/"})
    public List<Project> getProjects() {
        Session session = SessionFactoryProvider.establishSession();

        CriteriaQuery<Project> criteria = session.getCriteriaBuilder().createQuery(Project.class);
        criteria.from(Project.class);

        List<Project> list = session.createQuery(criteria).getResultList();

        session.close();

        for (Project item : list) {
            item.setNotes(null);
        }

        return list;
    }

    @PostMapping(value={"/project", "/project/"})
    public Project addProject(@RequestBody Project project) {
        Session session = SessionFactoryProvider.establishSession();

        session.getTransaction().begin();
        session.persist(project);
        session.getTransaction().commit();
        session.close();

        return project;
    }

    @PutMapping(value={"/project", "/project/"})
    public Project updateProject(@RequestBody Map<String, String> json) {
        Session session = SessionFactoryProvider.establishSession();

        Project project = session.get(Project.class, json.get("id"));
        project.setName(json.get("name"));

        session.beginTransaction();
        session.merge(project);
        session.getTransaction().commit();
        session.close();

        project.setNotes(null);

        return  project;
    }

    @DeleteMapping(value={"/project", "/project/"})
    public void deleteProject(@RequestBody Map<String, String> json) {
        Session session = SessionFactoryProvider.establishSession();

        Project project = session.get(Project.class, json.get("id"));

        Set<Note> notes = null;
        Hibernate.initialize(notes = project.getNotes());

        session.beginTransaction();

        for (Note note : notes) {
            session.remove(note);
        }

        session.remove(project);

        session.getTransaction().commit();
        session.close();
    }

    @GetMapping(value = "/project/{id}")
    public Project getProject(@PathVariable int id) {
        Session session = SessionFactoryProvider.establishSession();

        Project project = session.get(Project.class, id);
        Hibernate.initialize(project.getNotes());

        session.close();

        return project;
    }
}
