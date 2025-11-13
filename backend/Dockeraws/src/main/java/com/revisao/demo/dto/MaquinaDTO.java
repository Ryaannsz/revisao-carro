package com.revisao.demo.dto;

import java.sql.Date;

import com.revisao.demo.models.Marca;
import com.revisao.demo.models.Modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaquinaDTO {

    private Integer id;

    private Date dtAdicionado;

    private Double horas;

    private Marca marca;

    private Modelo modelo;

    private String chassi;

}
