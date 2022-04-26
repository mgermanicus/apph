package com.viseo.apph.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtConfig {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public JwtConfig() {

    }

    public static Key getKey() {
        return key;
    }
}
