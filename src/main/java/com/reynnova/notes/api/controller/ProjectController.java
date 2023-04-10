package com.reynnova.notes.api.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.reynnova.notes.service.JWTHelper;
import jakarta.persistence.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.hibernate.Hibernate;
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
    public ResponseEntity projects(@RequestHeader("Authorization") String token) {
        Session session = SessionProvider.get();

        List<Project> list;

        try {
            DecodedJWT decodedJWT = JWTHelper.verifyToken(token);

            Query query = session.createQuery("FROM Project P WHERE P.ownerId = " + decodedJWT.getSubject());

            list = query.getResultList();

            session.close();
        } catch (Exception error) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid token", null);
        }

        for (Project item : list) {
            item.setNotes(null);
        }

        return ResponseProvider.get(HttpStatus.OK, "Success get projects", list);
    }

    @PostMapping(value={"/create-project", "/create-project/"})
    public ResponseEntity addProject(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> json) {
        String projectName = json.get("name");

        if (projectName == null) {
            projectName = "";
        }

        Project project = new Project();
        project.setName(projectName);

        Session session = SessionProvider.get();

        Integer sessionUserId = SessionProvider.getSessionUserId(token);

        if (sessionUserId == null) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Invalid token", null);
        }

        project.setOwnerId(sessionUserId);

        session.getTransaction().begin();
        session.persist(project);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success create new project", project);
    }

    @PutMapping(value={"/project", "/project/"})
    public ResponseEntity updateProject(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> json) {
        String projectName = json.get("name");

        if (projectName == null || projectName.isBlank()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Project name is required", null);
        }

        Session session = SessionProvider.get();

        Integer sessionUserId = SessionProvider.getSessionUserId(token);

        if (sessionUserId == null) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Invalid token", null);
        }

        Project project = getProjectById(session, json.get("id"));

        if (project == null || sessionUserId != project.getOwnerId()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Wrong token or id", null);
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
    public ResponseEntity deleteProject(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Integer sessionUserId = SessionProvider.getSessionUserId(token);

        if (sessionUserId == null) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Invalid token", null);
        }

        Project project = getProjectById(session, json.get("id"));

        if (project == null || sessionUserId != project.getOwnerId()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Wrong token or id", null);
        }

        Set<Note> notes;
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

    @PostMapping(value={"/project", "/project/"})
    public ResponseEntity projectDetail(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Integer sessionUserId = SessionProvider.getSessionUserId(token);

        if (sessionUserId == null) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Invalid token", null);
        }

        Project project = getProjectById(session, json.get("id"));

        if (project == null || sessionUserId != project.getOwnerId()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Wrong token or id", null);
        }

        Hibernate.initialize(project.getNotes());

        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success get project", project);
    }

    static Project getProjectById(Session session, Object id) {
        Project project = null;

        try {
            project = session.get(Project.class, id);
        } catch (Exception error) {}

        return project;
    }
}
