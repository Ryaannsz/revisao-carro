package com.revisao.demo.dto;

import java.sql.Timestamp;

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
	

	private Integer idAbast;
	@NotNull
	private Double litroComb;
	@NotNull
	private Double valorComb;
	@NotNull
	private Double kmAtual;
	
	private Timestamp dtAbast;
	

	
	private User user;
	

	
	private Carro carro;
	



}
