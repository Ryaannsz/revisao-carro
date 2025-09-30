package com.revisao.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProximaRevisaoDTO {
    private Integer carroId;
    private String placa;
    private Date ultimaRevisao;
    private Double kmAtualEstimado;
    private Date proximaRevisaoEstimada;
}
