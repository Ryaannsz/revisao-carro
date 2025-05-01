package com.revisao.demo.dto;

import java.sql.Timestamp;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class CarroDTO {

	private Integer idCarro;
	@NotNull
	private Timestamp dtAdicionado;
	@NotNull
	private Double kmAdicionado;
	@NotNull
	private Integer idModelo;
	@NotNull
	private Integer idMarca;
	
}
