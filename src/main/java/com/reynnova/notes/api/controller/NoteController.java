package com.reynnova.notes.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.hibernate.Session;
import java.util.Map;

import com.reynnova.notes.api.model.Project;
import com.reynnova.notes.api.model.Note;
import com.reynnova.notes.service.ResponseProvider;
import com.reynnova.notes.service.SessionProvider;

@RestController
public class NoteController {
    @PostMapping(value={"/create-note", "/create-note/"})
    public ResponseEntity createNote(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Integer sessionUserId = SessionProvider.getSessionUserId(token);

        if (sessionUserId == null) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Invalid token", null);
        }

        Project project = ProjectController.getProjectById(session, json.get("projectId"));

        if (project == null || sessionUserId != project.getOwnerId()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Wrong token or projectId", null);
        }

        String noteValue = json.get("value");

        if (noteValue == null) {
            noteValue = "";
        }

        Note note = new Note();
        note.setValue(noteValue);
        note.setProjectId(project.getId());

        session.beginTransaction();
        session.persist(note);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success create new note", note);
    }

    @PutMapping(value={"/note", "/note/"})
    public ResponseEntity updateNote(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Integer sessionUserId = SessionProvider.getSessionUserId(token);

        if (sessionUserId == null) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Invalid token", null);
        }

        Note note = getNoteById(session, json.get("id"));

        if (note == null) {
            return ResponseProvider.get(HttpStatus.NOT_FOUND, "Wrong id", null);
        }

        Project project = ProjectController.getProjectById(session, note.getProjectId());

        if (project == null || sessionUserId != project.getOwnerId()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Wrong token", null);
        }

        String newTargetProjectId = json.get("projectId");

        if (newTargetProjectId != null) {
            Project newTargetProject = ProjectController.getProjectById(session, newTargetProjectId);

            if (newTargetProject == null || sessionUserId != newTargetProject.getOwnerId()) {
                return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Wrong token or projectId", null);
            }

            note.setProjectId(Integer.parseInt(newTargetProjectId));
        }

        note.setValue(json.get("value"));

        session.beginTransaction();
        session.merge(note);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success update note", note);
    }

    @DeleteMapping(value={"/note", "/note/"})
    public ResponseEntity deleteNote(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Integer sessionUserId = SessionProvider.getSessionUserId(token);

        if (sessionUserId == null) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Invalid token", null);
        }

        Note note = getNoteById(session, json.get("id"));

        if (note == null) {
            return ResponseProvider.get(HttpStatus.NOT_FOUND, "Wrong id", null);
        }

        Project project = ProjectController.getProjectById(session, note.getProjectId());

        if (project == null || sessionUserId != project.getOwnerId()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Wrong token", null);
        }

        session.beginTransaction();
        session.remove(note);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success delete note", null);
    }

    static Note getNoteById(Session session, Object id) {
        Note note = null;

        try {
            note = session.get(Note.class, id);
        } catch (Exception error) {

        }

        return note;
    }
}
