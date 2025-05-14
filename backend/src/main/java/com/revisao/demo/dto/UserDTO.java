package com.revisao.demo.dto;

import com.revisao.demo.enums.UserRoles;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

	private Integer idUser;
	
	@NotNull
	private String cpf;
	@NotNull
	private String nome;
	@NotNull
	private String senha;
	
	private UserRoles roles;
	
}
