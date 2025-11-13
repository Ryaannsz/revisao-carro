package com.revisao.demo.dto;

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

    private Date dtRevisao;

    private Double horasAtual;

    private User user;

    private Maquina maquina;

}
