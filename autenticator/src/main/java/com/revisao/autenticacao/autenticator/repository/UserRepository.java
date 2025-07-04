package com.revisao.autenticacao.autenticator.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revisao.autenticacao.autenticator.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

}
