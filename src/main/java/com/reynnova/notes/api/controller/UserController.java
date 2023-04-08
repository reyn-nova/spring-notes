package com.reynnova.notes.api.controller;

import com.reynnova.notes.api.model.User;
import jakarta.persistence.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import org.hibernate.Session;

import java.util.List;
import java.util.Map;

import com.reynnova.notes.service.ResponseProvider;
import com.reynnova.notes.service.SessionProvider;

@RestController
public class UserController {
    @PostMapping(value={"/sign-up", "/sign-up/"})
    public ResponseEntity signUp(@RequestBody Map<String, String> json) {
        String username = json.get("username");

        if (username == null || username.isBlank()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified username", null);
        }

        boolean hasNonAlphanumeric = username.matches("^[a-zA-Z0-9]*$");

        if (!hasNonAlphanumeric) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Only alphanumeric allowed in username", null);
        }

        String password = json.get("password");

        if (password == null || password.isBlank()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified password", null);
        }

        Session session = SessionProvider.get();

        Query query = session.createQuery("FROM User U Where U.username = '" + username + "'");
        List<User> list =  query.getResultList();

        if (list.size() > 0) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Username is already taken", null);
        }

        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(password, salt);

        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);

        session.getTransaction().begin();
        session.persist(user);
        session.getTransaction().commit();
        session.close();

        user.setPassword(null);

        return ResponseProvider.get(HttpStatus.OK, "Success create new user", user);
    }
}
