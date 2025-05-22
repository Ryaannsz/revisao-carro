package com.revisao.demo.dto;

import com.revisao.demo.enums.UserRoles;

import lombok.Data;

@Data
public class UpdateRoleDTO {

	private UserRoles roles;
	
}
