package com.reynnova.notes.api.controller;

import com.reynnova.notes.api.model.Note;
import com.reynnova.notes.service.ResponseProvider;
import com.reynnova.notes.service.SessionProvider;
import org.hibernate.Hibernate;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.reynnova.notes.api.model.Project;

@RestController
public class ProjectController {

    @GetMapping(value={"/project", "/project/"})
    public Map<String, Object> getProjects() {
        Session session = SessionProvider.get();

        CriteriaQuery<Project> criteria = session.getCriteriaBuilder().createQuery(Project.class);
        criteria.from(Project.class);

        List<Project> list = session.createQuery(criteria).getResultList();

        session.close();

        for (Project item : list) {
            item.setNotes(null);
        }

        return ResponseProvider.get("Success get projects", list);
    }

    @PostMapping(value={"/project", "/project/"})
    public Map<String, Object> addProject(@RequestBody Project project) {
        Session session = SessionProvider.get();

        session.getTransaction().begin();
        session.persist(project);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get("Success create new project", project);
    }

    @PutMapping(value={"/project", "/project/"})
    public Map<String, Object> updateProject(@RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Project project = session.get(Project.class, json.get("id"));
        project.setName(json.get("name"));

        session.beginTransaction();
        session.merge(project);
        session.getTransaction().commit();
        session.close();

        project.setNotes(null);

        return ResponseProvider.get("Success update project", project);
    }

    @DeleteMapping(value={"/project", "/project/"})
    public Map<String, Object> deleteProject(@RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

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

        return ResponseProvider.get("Success delete project", null);
    }

    @GetMapping(value = "/project/{id}")
    public Map<String, Object> getProject(@PathVariable int id) {
        Session session = SessionProvider.get();

        Project project = session.get(Project.class, id);
        Hibernate.initialize(project.getNotes());

        session.close();

        return ResponseProvider.get("Success get project", project);
    }
}
