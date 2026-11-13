package com.revisao.demo.infra.security;

import java.io.IOException;

import com.revisao.demo.models.User;
import com.revisao.demo.utils.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.revisao.demo.repository.UserRepository;
import com.revisao.demo.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Component
public class SecurityFilter extends OncePerRequestFilter {

	private final TokenService tokenService;

	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		var token = recoveryToken(request);
		if (token != null) {
			// subject = CPF (único). Token inválido/expirado => segue sem autenticação e o
			// entry point responde 401, fazendo o front redirecionar para o login.
			String cpf = tokenService.validateToken(token);
			if (!cpf.isBlank() && userRepository.findByCpf(cpf) instanceof User user) {
				var principal = new CustomUserPrincipal(user);
				var authen = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authen);
			}
		}
		filterChain.doFilter(request, response);
	}

	private String recoveryToken(HttpServletRequest request) {
		var authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
		return authHeader.substring(7);
	}
}
