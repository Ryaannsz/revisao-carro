package com.revisao.autenticacao.autenticator.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.revisao.autenticacao.autenticator.model.User;
import com.revisao.autenticacao.autenticator.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
	this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	User user = userRepository.findByUsername(username)
		.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

	Set<GrantedAuthority> authorities = user.getRoles().stream()
		.map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toSet());

	return new org.springframework.security.core.userdetails.User(user.getCpf(), user.getSenha(), user.isEnabled(),
		user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), authorities);
    }
}
