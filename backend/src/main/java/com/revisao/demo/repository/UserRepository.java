package com.revisao.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import com.revisao.demo.models.User;

import java.util.List;

public interface UserRepository extends BaseRepository<User, Integer>{
	
	UserDetails findByCpf(String cpf);

    @Query("select u.empresa.id from User u where u.cpf = :cpf")
    Integer findEmpresaIdByCpf(String cpf);


    UserDetails findByNome(String nome);

    List<User> findByEmpresaId(Integer empresaId);
}
