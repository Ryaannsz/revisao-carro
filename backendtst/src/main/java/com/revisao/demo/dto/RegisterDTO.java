package com.revisao.demo.dto;

import com.revisao.demo.enums.UserRoles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    private String nome;
    private String cpf;
    private String senha;
    private UserRoles roles;
}
