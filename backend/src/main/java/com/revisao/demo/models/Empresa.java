package com.revisao.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Empresa {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;

    private String cnpj;

    private Date dataExpiracao;

    private Boolean ativo;

}
