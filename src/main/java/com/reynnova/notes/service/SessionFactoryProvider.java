package com.reynnova.notes.service;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryProvider {
    public static SessionFactory provideSessionFactory()
    {
        Configuration config = new Configuration();

        config.setProperty("hibernate.connection.url", System.getenv("db_url"));
        config.setProperty("hibernate.connection.username",  System.getenv("db_username"));
        config.setProperty("hibernate.connection.password",  System.getenv("db_password"));

        config.configure();

        return config.buildSessionFactory();
    }
}