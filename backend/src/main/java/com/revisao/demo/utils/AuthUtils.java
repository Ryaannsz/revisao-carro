package com.revisao.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {
    private AuthUtils() {}

    public static String getEmpresaId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        // Aqui depende de como você armazenou o token no Authentication
        String token = (String) auth.getCredentials();

        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("empresaId").asString();
    }
}
