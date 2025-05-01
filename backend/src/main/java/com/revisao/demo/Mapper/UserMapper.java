package com.revisao.demo.Mapper;

import org.mapstruct.Mapper;

import com.revisao.demo.dto.UserDTO;
import com.revisao.demo.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserDTO, User>{

}
