package com.reynnova.notes.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTHelper {
    public static Algorithm getAlgorithm() {
        String jwtSecret = System.getenv("jwt.secret");

        return Algorithm.HMAC256(jwtSecret);
    }

    public static DecodedJWT verifyToken(String token) {
        JWTVerifier verifier = JWT.require(JWTHelper.getAlgorithm())
            .build();

        DecodedJWT decodedJWT = verifier.verify(token);

        return decodedJWT;
    }
}
