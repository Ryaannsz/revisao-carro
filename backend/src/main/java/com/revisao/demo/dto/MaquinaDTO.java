package com.revisao.demo.dto;

import com.revisao.demo.enums.MaquinaStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

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

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Sao_Paulo")
    private Date dtAdicionado;

    @NotNull
    @PositiveOrZero
    private Double horas;

    private Marca marca;

    private Modelo modelo;

    private String chassi;

    private String identificacao;

    private String tipo;

    private Integer ano;

    private MaquinaStatus status;

    private Double alarmeRevisaoHoras;

    private Integer alarmeRevisaoMes;

    private EmpresaDTO empresa;

}
