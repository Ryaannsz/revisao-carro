package com.revisao.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpresaDTO {

    private Integer id;

    private String nome;

    private String cnpj;

    private Date dataExpiracao;

    private Boolean ativo;
}
