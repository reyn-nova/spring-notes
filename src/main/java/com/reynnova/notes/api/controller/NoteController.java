package com.reynnova.notes.api.controller;

import com.reynnova.notes.api.model.Note;
import com.reynnova.notes.service.ResponseProvider;
import org.springframework.web.bind.annotation.*;

import org.hibernate.Session;
import java.util.Map;

import com.reynnova.notes.service.SessionProvider;

@RestController
public class NoteController {
    @PostMapping(value={"/note", "/note/"})
    public Map<String, Object> addNote(@RequestBody Note note) {
        Session session = SessionProvider.get();

        session.beginTransaction();
        session.persist(note);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get("Success create new note", note);
    }

    @PutMapping(value={"/note", "/note/"})
    public Map<String, Object> updateNote(@RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Note updatedNote = session.get(Note.class, json.get("id"));
        updatedNote.setValue(json.get("value"));

        if (json.get("projectId") != null) {
            updatedNote.setProjectId(Integer.parseInt(json.get("projectId")));
        }

        session.beginTransaction();
        session.merge(updatedNote);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get("Success update note", updatedNote);
    }

    @DeleteMapping(value={"/note", "/note/"})
    public Map<String, Object> deleteNote(@RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        Note note = session.get(Note.class, json.get("id"));

        session.beginTransaction();
        session.remove(note);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get("Success delete note", null);
    }
}
