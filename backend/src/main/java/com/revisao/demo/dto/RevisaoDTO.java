package com.revisao.demo.dto;

import java.sql.Timestamp;

import com.revisao.demo.models.Carro;
import com.revisao.demo.models.User;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevisaoDTO {

	private Integer idRevisao;
	@NotNull
	private Timestamp dtRevisao;
	@NotNull
	private Double litroComb;
	@NotNull
	private Double valorComb;
	@NotNull
	private Double kmAtual;
	@NotNull
	private Integer idUser;
	@NotNull
	private Integer idCarro;
	
	private User user;
	
	private Carro carro;
	
	
	
	
	
}
