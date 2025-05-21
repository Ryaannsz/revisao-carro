package com.revisao.demo.infra.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.revisao.demo.repository.UserRepository;
import com.revisao.demo.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter{

	@Autowired
	TokenService tokenService;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
        String uri = request.getRequestURI();

        if (uri.equals("/auth/login") || uri.equals("/auth/registro")) {
            filterChain.doFilter(request, response);
            return;
        }
		
		var token = this.recoveryToken(request);
		if(token != null) {
			var subject = tokenService.validateToken(token);
			UserDetails user = userRepository.findByCpf(subject);
			
			var authen = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authen);
		}
		filterChain.doFilter(request, response);
		
	}
	
	private String recoveryToken(HttpServletRequest request) {
		var authHeader = request.getHeader("Authorization");
		if(authHeader==null) return null;
		else return authHeader.replace("Bearer ", "");
	}
	
	
	
}