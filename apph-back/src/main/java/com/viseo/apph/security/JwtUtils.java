package com.viseo.apph.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private final int jwtExpirationMs = 7_200_000;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .claim("login", userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("firstname", userPrincipal.getFirstname())
                .claim("lastname", userPrincipal.getLastname())
                .claim("isAdmin", userPrincipal.isAdmin())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String setClaimOnToken(String token, Map<String, String> newClaims) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        JwtBuilder newToken = Jwts.builder().setClaims(claims);
        for (Map.Entry<String, String> entry : newClaims.entrySet()) {
            newToken.claim(entry.getKey(), entry.getValue());
        }
        return newToken.setExpiration(claims.getExpiration()).signWith(key).compact();
    }
}
