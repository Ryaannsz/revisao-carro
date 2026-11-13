package com.revisao.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.revisao.demo.enums.UserRoles;

import com.revisao.demo.models.Empresa;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Integer id;

    @NotNull
    private String cpf;
    @NotNull
    private String nome;
    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    private EmpresaDTO empresa;

    private UserRoles roles;

}
