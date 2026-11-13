package com.revisao.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.revisao.demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	private UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
		UserDetails user = userRepository.findByCpf(cpf);
		if (user == null) throw new UsernameNotFoundException("Usuário não encontrado");
		return user;
	}
	
}
