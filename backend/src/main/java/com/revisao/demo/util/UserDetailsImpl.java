package com.revisao.demo.util;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails{
	
	  private Integer id;
	  private String username;
	  private String nome;
	  private final Collection<? extends GrantedAuthority> authorities;
	  
	  	@Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        return authorities; 
	    }

	    @Override
	    public String getPassword() {
	        return null;
	    }

	    @Override
	    public String getUsername() {
	        return username;
	    }

	    @Override
	    public boolean isAccountNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isAccountNonLocked() {
	        return true;
	    }

	    @Override
	    public boolean isCredentialsNonExpired() {
	        return true;
	    }

	    @Override
	    public boolean isEnabled() {
	        return true;
	    }

}
