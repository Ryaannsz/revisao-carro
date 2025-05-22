package com.revisao.demo.repository;

import org.springframework.security.core.userdetails.UserDetails;

import com.revisao.demo.models.User;

public interface UserRepository extends BaseRepository<User, Integer>{
	
	UserDetails findByCpf(String cpf);

}
