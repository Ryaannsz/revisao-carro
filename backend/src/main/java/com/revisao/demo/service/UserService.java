package com.revisao.demo.service;

import org.springframework.stereotype.Service;

import com.revisao.demo.Mapper.UserMapper;
import com.revisao.demo.dto.UserDTO;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.UserRepository;

@Service
public class UserService extends BaseServiceImpl<UserDTO, User, Integer>{

    public UserService(UserRepository repository, UserMapper mapper) {
        super(repository, mapper);
    }
	
}
