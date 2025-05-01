package com.revisao.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revisao.demo.models.User;

public interface UserRepository extends BaseRepository<User, Integer>{

}
