package com.reynnova.notes.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.hibernate.Hibernate;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.reynnova.notes.api.model.Note;
import com.reynnova.notes.service.ResponseProvider;
import com.reynnova.notes.service.SessionProvider;
import com.reynnova.notes.api.model.Project;

@RestController
public class ProjectController {

    @GetMapping(value={"/project", "/project/"})
    public ResponseEntity getProjects() {
        Session session = SessionProvider.get();

        CriteriaQuery<Project> criteria = session.getCriteriaBuilder().createQuery(Project.class);
        criteria.from(Project.class);

        List<Project> list = session.createQuery(criteria).getResultList();

        session.close();

        for (Project item : list) {
            item.setNotes(null);
        }

        return ResponseProvider.get(HttpStatus.OK, "Success get projects", list);
    }

    @PostMapping(value={"/project", "/project/"})
    public ResponseEntity addProject(@RequestBody Map<String, String> json) {
        String projectName = json.get("name");

        if (projectName == null) {
            projectName = "";
        }

        Project project = new Project();
        project.setName(projectName);

        Session session = SessionProvider.get();
        session.getTransaction().begin();
        session.persist(project);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success create new project", project);
    }

    @PutMapping(value={"/project", "/project/"})
    public ResponseEntity updateProject(@RequestBody Map<String, String> json) {
        String projectName = json.get("name");

        if (projectName == null || projectName.isBlank()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Project name is required", null);
        }

        Session session = SessionProvider.get();

        Project project;

        try {
            project = session.get(Project.class, json.get("id"));

            if (project == null) {
                return ResponseProvider.get(HttpStatus.NOT_FOUND, "Project not found", null);
            }
        } catch (Exception error) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid id", null);
        }

        project.setName(projectName);

        session.beginTransaction();
        session.merge(project);
        session.getTransaction().commit();
        session.close();

        project.setNotes(null);

        return ResponseProvider.get(HttpStatus.OK, "Success update project", project);
    }

    @DeleteMapping(value={"/project", "/project/"})
    public ResponseEntity deleteProject(@RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Project project;

        try {
            project = session.get(Project.class, json.get("id"));

            if (project == null) {
                return ResponseProvider.get(HttpStatus.NOT_FOUND, "Project not found", null);
            }
        } catch (Exception error) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid id", null);
        }

        Set<Note> notes = null;
        Hibernate.initialize(notes = project.getNotes());

        session.beginTransaction();

        for (Note note : notes) {
            session.remove(note);
        }

        session.remove(project);

        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success delete project", null);
    }

    @GetMapping(value = "/project/{id}")
    public ResponseEntity getProject(@PathVariable Object id) {
        Session session = SessionProvider.get();

        Project project;

        try {
            project = session.get(Project.class, id);

            if (project == null) {
                return ResponseProvider.get(HttpStatus.NOT_FOUND, "Project not found", null);
            }
        } catch (Exception error) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid id", null);
        }

        Hibernate.initialize(project.getNotes());

        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success get project", project);
    }
}
