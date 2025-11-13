package com.revisao.demo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.revisao.demo.dto.EmpresaDTO;
import com.revisao.demo.mapper.EmpresaMapper;
import com.revisao.demo.models.Empresa;
import com.revisao.demo.service.EmpresaService;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {
    private AuthUtils(){}

    public static Integer getEmpresaId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        if (auth.getPrincipal() instanceof CustomUserPrincipal p) {

            return Integer.parseInt(p.getEmpresaId());
        }
        throw new IllegalStateException("Principal inesperado: " + auth.getPrincipal());
    }

}
