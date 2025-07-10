package com.revisao.demo.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.revisao.demo.models.Marca;
import com.revisao.demo.models.Modelo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CarroDTO {

    private Integer id;

    @JsonFormat(pattern = "dd-MM-yyyy", timezone = "America/Sao_Paulo")
    private Date dtAdicionado;

    @NotNull
    private Double kmAdicionado;

    private Marca marca;

    private Modelo modelo;

    private String placa;

}
