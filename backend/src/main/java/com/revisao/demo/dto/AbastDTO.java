package com.revisao.demo.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.User;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbastDTO {

    private Integer id;
    @NotNull
    private Double litroComb;
    @NotNull
    private Double valorComb;
    @NotNull
    private Double kmAtual;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Sao_Paulo")
    private Date dtAbast;

    private User user;

    private Carro carro;

}
