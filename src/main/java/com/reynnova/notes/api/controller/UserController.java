package com.reynnova.notes.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import org.hibernate.Session;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.Query;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.reynnova.notes.api.model.User;
import com.reynnova.notes.service.JWTHelper;
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

        return ResponseProvider.get(HttpStatus.OK, "Success create new user", null);
    }

    @PostMapping(value={"/sign-in", "/sign-in/"})
    public ResponseEntity signIn(@RequestBody Map<String, String> json) {
        String username = json.get("username");

        if (username == null || username.isBlank()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified username", null);
        }

        String password = json.get("password");

        if (password == null) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified password", null);
        }

        Session session = SessionProvider.get();

        Query query = session.createQuery("FROM User U Where U.username = '" + username + "'");
        List<User> list =  query.getResultList();

        session.close();

        if (list.size() == 0) {
            return ResponseProvider.get(HttpStatus.NOT_FOUND, "User not found", null);
        }

        User user = list.get(0);

        Boolean isPasswordMatch = BCrypt.checkpw(password, user.getPassword());

        if (!isPasswordMatch) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Wrong password", null);
        }

        user.setPassword(null);

        String token = JWT.create()
            .withSubject(Integer.toString(user.getId()))
            .withIssuedAt(new Date()).sign(JWTHelper.getAlgorithm());

        Map<String, Object> data = new HashMap<>();

        data.put("user", user);
        data.put("token", token);

        return ResponseProvider.get(HttpStatus.OK, "Success sign in", data);
    }

    @GetMapping(value={"/user-detail", "/user-detail/"})
    public ResponseEntity userDetail(@RequestHeader("Authorization") String token) {
        Session session = SessionProvider.get();

        User user;

        try {
            DecodedJWT decodedJWT = JWTHelper.verifyToken(token);

            user = session.get(User.class, decodedJWT.getSubject());

            if (user == null) {
                return ResponseProvider.get(HttpStatus.NOT_FOUND, "User not found", null);
            }
        } catch (Exception error) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid token", null);
        }

        session.close();

        user.setPassword(null);

        return ResponseProvider.get(HttpStatus.OK, "Success get user detail", user);
    }

    @PutMapping(value= {"/update-user-detail", "/update-user-detail/"})
    public ResponseEntity updateUserDetail(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> json) {
        Session session = SessionProvider.get();

        User user;

        try {
            DecodedJWT decodedJWT = JWTHelper.verifyToken(token);

            user = session.get(User.class, decodedJWT.getSubject());

            if (user == null) {
                return ResponseProvider.get(HttpStatus.NOT_FOUND, "User not found", null);
            }
        } catch (Exception error) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid token", null);
        }

        String username = json.get("username");

        if (username == null || username.isBlank()) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified username", null);
        }

        boolean hasNonAlphanumeric = username.matches("^[a-zA-Z0-9]*$");

        if (!hasNonAlphanumeric) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Only alphanumeric allowed in username", null);
        }

        Query query = session.createQuery("FROM User U Where U.username = '" + username + "'");
        List<User> list =  query.getResultList();

        Boolean isUsernameTakenByOther = list.size() > 0 && list.get(0).getId() != user.getId();

        if (isUsernameTakenByOther) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Username is already taken", null);
        }

        user.setUsername(username);

        session.beginTransaction();
        session.merge(user);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success update user", null);
    }

    @DeleteMapping(value={"/delete-user", "/delete-user"})
    public ResponseEntity deleteUser(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> json) {
        String password = json.get("password");
        String confirmPassword = json.get("confirmPassword");

        if (password == null) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified password", null);
        }

        if (confirmPassword == null) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified confirmPassword", null);
        }

        Session session = SessionProvider.get();

        User user;

        try {
            DecodedJWT decodedJWT = JWTHelper.verifyToken(token);

            user = session.get(User.class, decodedJWT.getSubject());

            if (user == null) {
                return ResponseProvider.get(HttpStatus.NOT_FOUND, "User not found", null);
            }
        } catch (Exception error) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Unspecified or invalid token", null);
        }

        Boolean isPasswordMatch = password.equals(confirmPassword) && BCrypt.checkpw(password, user.getPassword());

        if (!isPasswordMatch) {
            return ResponseProvider.get(HttpStatus.BAD_REQUEST, "Wrong password or wrong confirmPassword", null);
        }

        session.beginTransaction();
        session.remove(user);
        session.getTransaction().commit();
        session.close();

        return ResponseProvider.get(HttpStatus.OK, "Success delete user", null);
    }
}
