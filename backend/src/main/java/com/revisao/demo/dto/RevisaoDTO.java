package com.revisao.demo.dto;

import java.sql.Timestamp;

import com.revisao.demo.models.Carro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevisaoDTO {

    private Integer idRevisao;

    private Timestamp dtRevisao;

    private Double kmAtual;

    private String userId;

    private Carro carro;

}
