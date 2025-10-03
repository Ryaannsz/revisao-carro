package com.revisao.demo.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevisaoDTO {

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Sao_Paulo")
    private Date dtRevisao;

    private Double kmAtual;

    private User user;

    private Carro carro;

}
