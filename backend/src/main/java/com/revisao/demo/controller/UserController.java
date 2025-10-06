package com.revisao.demo.controller;


import java.util.List;

import com.revisao.demo.utils.AuthUtils;
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

import javax.naming.CannotProceedException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	
	@GetMapping
	public List<UserDTO> getAll(){
		return userService.getUsersByEmpresa(AuthUtils.getEmpresaId());
	}
	
	@PostMapping
	public ResponseEntity<Void> saveUser(@Valid @RequestBody UserDTO user){
		userService.saveUser(user, AuthUtils.getEmpresaId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<Void> patchUser(@PathVariable Integer id, @RequestBody UpdateRoleDTO role) throws CannotProceedException {
		userService.updateUserRole(id, role.getRoles(), AuthUtils.getEmpresaId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
}
