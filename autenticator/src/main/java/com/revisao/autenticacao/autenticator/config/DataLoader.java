package com.revisao.autenticacao.autenticator.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.revisao.autenticacao.autenticator.enums.UserRoles;
import com.revisao.autenticacao.autenticator.model.User;
import com.revisao.autenticacao.autenticator.repository.UserRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
	return args -> {

	    if (userRepository.findByUsername("user").isEmpty()) {
		Set<UserRoles> roles = new HashSet<>(Collections.singletonList(UserRoles.ADMIN));
		User user = new User(1, "admin", "123.456.789-01", passwordEncoder.encode("admin"), roles, true, true,
			true, true);
		userRepository.save(user);
		System.out.println("Usuário 'user' e ROLE_ADMIN criados.");
	    }
	};
    }
}