package com.revisao.demo.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
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

	private Timestamp dtRevisao;
	
	private Double kmAtual;
	
	@NotNull @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Integer idUser;
	
	@NotNull @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Integer idCarro;
	
	private User user;
	
	private Carro carro;
	
	
	
	
	
}
