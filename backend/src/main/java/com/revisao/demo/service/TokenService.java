package com.revisao.demo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.revisao.demo.util.UserDetailsImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public TokenService(@Value("${api.security.token.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm)
                .withIssuer("auth-api-aciel")
                .build();
    }

    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("nome").asString();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token inválido ou expirado", e);
        }
    }

    public Integer getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("id").asInt(); 
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token inválido ou expirado", e);
        }
    }

    public String getRoleFromToken(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("role").asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
    
    public UserDetailsImpl parseUserFromToken(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);

            Integer id = jwt.getClaim("id").asInt();
            String username = jwt.getClaim("username").asString();
            String nome = jwt.getClaim("nome").asString();
            String rolesString = jwt.getClaim("roles").asString(); 

            List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesString.replace("[", "").replace("]", "").split(","))
                    .map(String::trim)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return new UserDetailsImpl(id, username, nome, authorities);

        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token inválido", e);
        }
    }
}
