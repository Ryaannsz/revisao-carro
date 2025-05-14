package com.revisao.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.revisao.demo.dto.AuthResponseDTO;
import com.revisao.demo.dto.AuthenticatorDTO;
import com.revisao.demo.dto.RegisterDTO;
import com.revisao.demo.mapper.UserMapper;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.UserRepository;

@Service
public class AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	TokenService tokenService;
	
	
	public AuthResponseDTO authUser(AuthenticatorDTO data) {
		var usernamePassword = new UsernamePasswordAuthenticationToken(data.getCpf(), data.getSenha());
		var auth = this.authenticationManager.authenticate(usernamePassword);
		var token = tokenService.generateToken((User)auth.getPrincipal());
		
		return new AuthResponseDTO(token);
	}
	
	public void registerUser(RegisterDTO data) {
		if(this.userRepository.findByCpf(data.getCpf()) != null)
			throw new IllegalStateException("Cpf já cadastrado");
		
		String passwordHash = new BCryptPasswordEncoder().encode(data.getSenha());
		User user = new User();
		user.setCpf(data.getCpf());
		user.setNome(data.getNome());
		user.setSenha(passwordHash);
		user.setRoles(data.getRoles());
		this.userRepository.save(user);
	}
	
	

}
