package com.viseo.apph.controller;

import com.viseo.apph.config.JwtConfig;
import io.jsonwebtoken.Jwts;

public interface TokenManager {
    default int getIdOfToken(String token){
        return (int) Jwts.parserBuilder().setSigningKey(JwtConfig.getKey()).build().parseClaimsJws(token).getBody().get("id");
    };
}
