package com.reynnova.notes.api.controller;

import com.reynnova.notes.api.model.Note;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import java.util.List;

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
}
