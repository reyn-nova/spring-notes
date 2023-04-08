package com.reynnova.notes.service;

import org.hibernate.cfg.Configuration;
import org.hibernate.Session;

public class SessionProvider {
    public static Session get()
    {
        Configuration config = new Configuration();

        config.setProperty("hibernate.connection.url", System.getenv("db_url"));
        config.setProperty("hibernate.connection.username",  System.getenv("db_username"));
        config.setProperty("hibernate.connection.password",  System.getenv("db_password"));

        config.configure();

        Session session = config.buildSessionFactory().openSession();

        return session;
    }
}