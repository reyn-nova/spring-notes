package com.reynnova.notes.api.controller;

import com.reynnova.notes.api.model.Note;
import org.springframework.web.bind.annotation.*;

import org.hibernate.Session;
import java.util.Map;

import com.reynnova.notes.service.SessionFactoryProvider;

@RestController
public class NoteController {
    @PostMapping(value={"/note", "/note/"})
    public Note addNote(@RequestBody Note note) {
        Session session = SessionFactoryProvider.establishSession();

        session.beginTransaction();
        session.persist(note);
        session.getTransaction().commit();
        session.close();

        return note;
    }

    @PutMapping(value={"/note", "/note/"})
    public Note updateNote(@RequestBody Map<String, String> json) {
        Session session = SessionFactoryProvider.establishSession();

        Note updatedNote = session.get(Note.class, json.get("id"));
        updatedNote.setValue(json.get("value"));

        if (json.get("projectId") != null) {
            updatedNote.setProjectId(Integer.parseInt(json.get("projectId")));
        }

        session.beginTransaction();
        session.merge(updatedNote);
        session.getTransaction().commit();
        session.close();

        return updatedNote;
    }

    @DeleteMapping(value={"/note", "/note/"})
    public void deleteNote(@RequestBody Map<String, String> json) {
        Session session = SessionFactoryProvider.establishSession();

        Note note = session.get(Note.class, json.get("id"));

        session.beginTransaction();
        session.remove(note);
        session.getTransaction().commit();
        session.close();
    }
}
