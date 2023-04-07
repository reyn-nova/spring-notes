package com.reynnova.notes.api.controller;

import com.reynnova.notes.api.model.Project;
import com.reynnova.notes.service.SessionFactoryProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProjectController {

    @GetMapping(value={"/project", "/project/"}, params = "!id")
    public List<Project> getProjects() {
        SessionFactory sessionFactory = SessionFactoryProvider.provideSessionFactory();
        Session session = sessionFactory.openSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Project> criteria = builder.createQuery(Project.class);
        criteria.from(Project.class);

        List<Project> list = session.createQuery(criteria).getResultList();

        session.close();

        return list;
    }

    @GetMapping(value = "/project", params = "id")
    public Project getProject(@RequestParam Integer id) {
        SessionFactory sessionFactory = SessionFactoryProvider.provideSessionFactory();
        Session session = sessionFactory.openSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Project> criteria = builder.createQuery(Project.class);
        criteria.from(Project.class);

        Project project = session.get(Project.class, id);

        session.close();

        return project;
    }
}
