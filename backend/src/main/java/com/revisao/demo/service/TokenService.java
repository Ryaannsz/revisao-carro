package com.revisao.demo.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.revisao.demo.models.User;

@Service
public class TokenService {

    private static final String ISSUER = "auth-api-aciel";

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-hours:8}")
    private long expirationHours;

    public String generateToken(User user) {
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    // CPF é único; antes era o nome, o que confundia usuários homônimos
                    .withSubject(user.getCpf())
                    .withClaim("role", user.getRoles().toString())
                    .withClaim("nome", user.getNome())
                    .withClaim("idUser", user.getId())
                    .withClaim("empresaId", user.getEmpresa().getId())
                    .withExpiresAt(Instant.now().plus(expirationHours, ChronoUnit.HOURS))
                    .sign(Algorithm.HMAC256(secret));
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao criar o token", exception);
        }
    }

    /** @return CPF do usuário, ou string vazia se o token for inválido/expirado. */
    public String validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secret)).withIssuer(ISSUER).build().verify(token).getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }
}
