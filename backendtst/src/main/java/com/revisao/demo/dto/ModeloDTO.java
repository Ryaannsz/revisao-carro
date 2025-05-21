package com.revisao.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModeloDTO {

	private Integer idModelo;
	
	@NotNull
	private String modelo;
	
	
}
