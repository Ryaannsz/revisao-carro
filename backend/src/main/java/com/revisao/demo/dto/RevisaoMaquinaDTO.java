package com.revisao.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.sql.Date;

import com.revisao.demo.models.Maquina;
import com.revisao.demo.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevisaoMaquinaDTO {

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Sao_Paulo")
    private Date dtRevisao;

    @NotNull
    @PositiveOrZero
    private Double horasAtual;

    @PositiveOrZero
    private Double valor;

    @Size(max = 1000)
    private String descricao;

    private User user;

    private Maquina maquina;

}
