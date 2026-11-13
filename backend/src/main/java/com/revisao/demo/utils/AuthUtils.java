package com.revisao.demo.utils;

import com.revisao.demo.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {
    private AuthUtils(){}

    private static CustomUserPrincipal principal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserPrincipal p)) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        return p;
    }

    public static Integer getEmpresaId() {
        Integer empresaId = principal().getEmpresaId();
        if (empresaId == null) throw new IllegalStateException("Usuário sem empresa vinculada");
        return empresaId;
    }

    public static Integer getUserId() {
        return principal().getUserId();
    }

    public static User getUser() {
        return principal().getUser();
    }
}
