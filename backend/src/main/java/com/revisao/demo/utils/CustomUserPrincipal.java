package com.revisao.demo.utils;

import com.revisao.demo.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Principal autenticado. Guarda o usuário carregado do banco para que
 * empresa e id venham da fonte de verdade, e não de claims do token.
 */
public class CustomUserPrincipal implements UserDetails {
    private final User user;

    public CustomUserPrincipal(User user) {
        this.user = user;
    }

    public User getUser() { return user; }
    public Integer getUserId() { return user.getId(); }
    public Integer getEmpresaId() { return user.getEmpresa() != null ? user.getEmpresa().getId() : null; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities(){ return user.getAuthorities(); }
    @Override public String getPassword(){ return user.getPassword(); }
    @Override public String getUsername(){ return user.getUsername(); }
    @Override public boolean isAccountNonExpired(){ return user.isAccountNonExpired(); }
    @Override public boolean isAccountNonLocked(){ return user.isAccountNonLocked(); }
    @Override public boolean isCredentialsNonExpired(){ return user.isCredentialsNonExpired(); }
    @Override public boolean isEnabled(){ return user.isEnabled(); }
}
