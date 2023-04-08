package com.reynnova.notes.service;

import java.util.HashMap;
import java.util.Map;

public class ResponseProvider {
    static public Map<String, Object> get(String message, Object data) {
        Map<String, Object> object = new HashMap<>();

        object.put("message", message);
        object.put("data", data);

        return object;
    }
}
