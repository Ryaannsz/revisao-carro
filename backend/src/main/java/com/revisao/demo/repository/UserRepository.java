package com.revisao.demo.repository;

import org.springframework.security.core.userdetails.UserDetails;

import com.revisao.demo.models.User;

import java.util.List;

public interface UserRepository extends BaseRepository<User, Integer>{
	
	UserDetails findByCpf(String cpf);


    List<User> findByEmpresaId(Integer empresaId);

    boolean existsByCpf(String cpf);
}
