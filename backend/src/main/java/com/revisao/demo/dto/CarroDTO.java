package com.revisao.demo.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revisao.demo.models.Marca;
import com.revisao.demo.models.Modelo;

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
	@NotNull @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Integer idModelo;
	@NotNull @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Integer idMarca;
	
	private Marca marca;
	
	private Modelo modelo;
	
}
