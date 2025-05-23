package com.revisao.demo.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revisao.demo.models.Marca;
import com.revisao.demo.models.Modelo;
import com.revisao.demo.models.User;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class CarroDTO {

	private Integer idCarro;

	private Timestamp dtAdicionado;
	@NotNull
	private Double kmAdicionado;

	
	private Marca marca;
	
	private Modelo modelo;
	
	private String placa;
	
	
}
