package com.revisao.demo.utils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserPrincipal implements UserDetails {
    private final UserDetails delegate;
    private final String empresaId;

    public CustomUserPrincipal(UserDetails delegate, String empresaId) {
        this.delegate = delegate;
        this.empresaId = empresaId;
    }
    public String getEmpresaId() { return empresaId; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities(){ return delegate.getAuthorities(); }
    @Override public String getPassword(){ return delegate.getPassword(); }
    @Override public String getUsername(){ return delegate.getUsername(); }
    @Override public boolean isAccountNonExpired(){ return delegate.isAccountNonExpired(); }
    @Override public boolean isAccountNonLocked(){ return delegate.isAccountNonLocked(); }
    @Override public boolean isCredentialsNonExpired(){ return delegate.isCredentialsNonExpired(); }
    @Override public boolean isEnabled(){ return delegate.isEnabled(); }
}
