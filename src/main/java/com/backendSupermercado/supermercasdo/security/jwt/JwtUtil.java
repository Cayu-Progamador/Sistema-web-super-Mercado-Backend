package com.backendSupermercado.supermercasdo.security.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // 🔐 SECRET EN BASE64 (OBLIGATORIO PARA HS256 BIEN HECHO)
    private static final String SECRET =
            "c3VwZXJtZXJjYWRvX2p3dF9zZWNyZXRfa2V5XzIwMjZfc2VndXJhX2Jvb2s=";

    // 1 HORA
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    // 🔑 CLAVE SEGURA
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET)
        );
    }

    // =========================
    // GENERAR TOKEN
    // =========================
    public String generateToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // =========================
    // EXTRAER USERNAME
    // =========================
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // =========================
    // VALIDAR TOKEN
    // =========================
    public boolean isTokenValid(String token, UserDetails userDetails) {

        final String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    // =========================
    // EXPIRACIÓN
    // =========================
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    // =========================
    // CLAIMS
    // =========================
    private Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}