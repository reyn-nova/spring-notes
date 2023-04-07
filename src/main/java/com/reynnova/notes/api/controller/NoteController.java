package com.reynnova.notes.api.controller;

import com.reynnova.notes.api.model.Note;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import java.util.List;

import com.reynnova.notes.service.SessionFactoryProvider;

@RestController
public class NoteController {

    @GetMapping(value={"/notes", "/notes/"})
    public List<Note> getNotes() {
        Session session = SessionFactoryProvider.establishSession();

        CriteriaQuery<Note> criteria = session.getCriteriaBuilder().createQuery(Note.class);
        criteria.from(Note.class);

        List<Note> list = session.createQuery(criteria).getResultList();

        session.close();

        return list;
    }
}
