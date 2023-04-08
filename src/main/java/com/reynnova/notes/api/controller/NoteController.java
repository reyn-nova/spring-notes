package com.reynnova.notes.api.controller;

import com.reynnova.notes.api.model.Project;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.hibernate.Session;
import java.util.Map;

import com.reynnova.notes.api.model.Note;
import com.reynnova.notes.service.ResponseProvider;
import com.reynnova.notes.service.SessionProvider;

@RestController
public class NoteController {
    @PostMapping(value={"/note", "/note/"})
    public ResponseEntity addNote(@RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Project project;

        Object projectId = json.get("projectId");

        try {
            project = session.get(Project.class, projectId);

            if (project == null) {
                return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Project with specified projectId not found", null);
            }
        } catch (Exception error) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid projectId", null);
        }

        String noteValue = json.get("value");

        if (noteValue == null) {
            noteValue = "";
        }

        Note note = new Note();
        note.setValue(noteValue);
        note.setProjectId(Integer.parseInt((String) projectId));

        session.beginTransaction();
        session.persist(note);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success create new note", note);
    }

    @PutMapping(value={"/note", "/note/"})
    public ResponseEntity updateNote(@RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Note note;

        try {
            note = session.get(Note.class, json.get("id"));

            if (note == null) {
                return ResponseProvider.get(HttpStatus.NOT_FOUND, "Note not found", null);
            }
        } catch (Exception error) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid id", null);
        }

        note.setValue(json.get("value"));

        Object projectId = json.get("projectId");

        if (projectId != null) {
            Project project;

            try {
                project = session.get(Project.class, projectId);

                if (project == null) {
                    return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Project with specified projectId not found", null);
                }
            } catch (Exception error) {
                return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid projectId", null);
            }

            note.setProjectId(Integer.parseInt((String) projectId));
        }

        session.beginTransaction();
        session.merge(note);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success update note", note);
    }

    @DeleteMapping(value={"/note", "/note/"})
    public ResponseEntity deleteNote(@RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Note note;

        try {
            note = session.get(Note.class, json.get("id"));

            if (note == null) {
                return ResponseProvider.get(HttpStatus.NOT_FOUND, "Note not found", null);
            }
        } catch (Exception error) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid id", null);
        }

        session.beginTransaction();
        session.remove(note);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success delete note", null);
    }
}
