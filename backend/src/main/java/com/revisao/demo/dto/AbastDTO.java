package com.revisao.demo.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbastDTO {

    private Integer id;
    @NotNull
    @Positive
    private Double litroComb;

    /** Valor TOTAL pago no abastecimento (não é preço por litro). */
    @NotNull
    @Positive
    private Double valorComb;

    @NotNull
    @Positive
    private Double kmAtual;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Sao_Paulo")
    private Date dtAbast;

    private User user;

    private Carro carro;

}
