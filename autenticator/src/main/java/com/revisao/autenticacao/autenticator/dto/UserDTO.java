package com.revisao.autenticacao.autenticator.dto;

import com.revisao.autenticacao.autenticator.enums.UserRoles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer id;

    private String username;
    private String nome;

    private UserRoles role;

}
