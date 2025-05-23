package com.revisao.demo.service;

import org.springframework.stereotype.Service;


import com.revisao.demo.dto.UserDTO;
import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.mapper.UserMapper;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.UserRepository;

@Service
public class UserService extends BaseServiceImpl<UserDTO, User, Integer>{

	private UserRepository repository;
	
    public UserService(UserRepository repository, UserMapper mapper) {
        super(repository, mapper);
        this.repository=repository;
    }
    
    public void updateUserRole(Integer id, UserRoles newRole) {
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        user.setRoles(newRole); 
        repository.save(user); 
    }
    
}
