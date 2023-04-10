package com.reynnova.notes.service;

import com.auth0.jwt.interfaces.DecodedJWT;
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

    public static Integer getSessionUserId(String token) {
        Integer sessionUserId = null;

        try {
            DecodedJWT decodedJWT = JWTHelper.verifyToken(token);

            sessionUserId = Integer.parseInt(decodedJWT.getSubject());
        } catch (Exception error) {}

        return sessionUserId;
    }
}