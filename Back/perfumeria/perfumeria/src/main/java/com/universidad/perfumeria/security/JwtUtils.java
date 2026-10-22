package com.universidad.perfumeria.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Leeremos estos valores desde el archivo de configuración (application.properties)
    @Value("${perfumeria.app.jwtSecret}")
    private String jwtSecret;

    @Value("${perfumeria.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // 1. Generar el Token cuando el usuario hace login
    public String generarTokenJwt(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(obtenerClaveFirma(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. Extraer la clave secreta de forma segura
    private Key obtenerClaveFirma() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 3. Obtener el nombre de usuario a partir del token
    public String obtenerUsernameDelToken(String token) {
        return Jwts.parserBuilder().setSigningKey(obtenerClaveFirma()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 4. Validar que el token sea auténtico y no haya expirado
    public boolean validarTokenJwt(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(obtenerClaveFirma()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Token JWT inválido o expirado: " + e.getMessage());
        }
        return false;
    }
}