package com.revisao.demo.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.revisao.demo.models.User;

@Service

public class TokenService {

	@Value("${api.security.token.secret}")
	private String secret;
	
	public String generateToken(User user) {
		try {
			Algorithm algoritimo = Algorithm.HMAC256(secret);
			String token = JWT.create()
					.withIssuer("auth-api-aciel")
					.withSubject(user.getNome())
					.withClaim("role", user.getRoles().toString())
					.withClaim("nome", user.getUsername())
					.withClaim("idUser", user.getIdUser())
					.withExpiresAt(genExpiresToken())
					.sign(algoritimo);
			return token;
		}catch(JWTCreationException exception) {
			throw new RuntimeException("Erro ao criar o token", exception);
		}
	}
	
	public String validateToken(String token) {
		try {
			Algorithm algoritimo = Algorithm.HMAC256(secret);
			return JWT.require(algoritimo)
					.withIssuer("auth-api-aciel")
					.build()
					.verify(token)
					.getSubject();
			
			
		}catch(JWTVerificationException exception) {
			return "";
		}
	}
	
	private Instant genExpiresToken() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}
	
	
}