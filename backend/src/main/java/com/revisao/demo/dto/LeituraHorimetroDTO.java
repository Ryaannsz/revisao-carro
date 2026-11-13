package com.revisao.demo.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeituraHorimetroDTO {
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Sao_Paulo")
    private Date dtLeitura;

    @NotNull
    @PositiveOrZero
    private Double horas;

    /** Nome de quem registrou (somente leitura). */
    private String usuario;
}
