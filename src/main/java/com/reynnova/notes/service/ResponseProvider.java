package com.reynnova.notes.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseProvider {
    static public ResponseEntity get(HttpStatus status, String message, Object data) {
        Map<String, Object> object = new HashMap<>();

        object.put("message", message);
        object.put("data", data);

        return new ResponseEntity(object, status);
    }
}
