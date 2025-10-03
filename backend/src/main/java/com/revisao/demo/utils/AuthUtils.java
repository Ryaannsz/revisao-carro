package com.revisao.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {
    private AuthUtils() {}

    public static String getEmpresaId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        if (auth.getPrincipal() instanceof CustomUserPrincipal p) {

            return p.getEmpresaId();
        }
        throw new IllegalStateException("Principal inesperado: " + auth.getPrincipal());
    }
}
