package com.revisao.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revisao.demo.dto.AuthenticatorDTO;
import com.revisao.demo.dto.RegisterDTO;
import com.revisao.demo.repository.UserRepository;
import com.revisao.demo.service.AuthService;
import com.revisao.demo.service.TokenService;

@RestController
@RequestMapping("auth")
public class AuthController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity login(@RequestBody AuthenticatorDTO data) {
		return ResponseEntity.ok(authService.authUser(data));
	}
	
	@PostMapping("/registro")
	public ResponseEntity register(@RequestBody RegisterDTO data) {
		authService.registerUser(data);
		return ResponseEntity.ok().build();
		
	}

}
