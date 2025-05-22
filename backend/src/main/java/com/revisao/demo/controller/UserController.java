package com.revisao.demo.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revisao.demo.dto.UpdateRoleDTO;
import com.revisao.demo.dto.UserDTO;
import com.revisao.demo.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	
	@GetMapping
	public List<UserDTO> getAll(){
		return userService.findAll();
	}
	
	@PostMapping
	public ResponseEntity<Void> saveUser(@Valid @RequestBody UserDTO user){
		userService.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<Void> patchUser(@PathVariable Integer id, @RequestBody UpdateRoleDTO role){
		userService.updateUserRole(id, role.getRoles());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
}
