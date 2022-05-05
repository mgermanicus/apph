package com.viseo.apph.controller;

import com.viseo.apph.config.JwtConfig;
import io.jsonwebtoken.Jwts;

import java.security.SignatureException;

public interface TokenManager {
    default int getIdOfToken(String token){
        return (int) Jwts.parserBuilder().setSigningKey(JwtConfig.getKey()).build().parseClaimsJws(token).getBody().get("id");
    }

    default String getLoginOfToken(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(JwtConfig.getKey()).build().parseClaimsJws(token).getBody().get("login");
    }
}
