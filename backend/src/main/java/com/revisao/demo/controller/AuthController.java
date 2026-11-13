package com.revisao.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revisao.demo.dto.AuthResponseDTO;
import com.revisao.demo.dto.AuthenticatorDTO;
import com.revisao.demo.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthenticatorDTO data) {
	return ResponseEntity.ok(authService.authUser(data));
    }

    // O antigo POST /auth/registro foi removido: ele aceitava "roles" e "empresa" vindos do
    // cliente (qualquer um poderia se criar ADMIN em qualquer empresa). Usuários agora são
    // criados apenas por um ADMIN via POST /user.

}
