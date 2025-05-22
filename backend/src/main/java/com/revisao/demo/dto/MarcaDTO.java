package com.revisao.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcaDTO {

	private Integer idMarca;
	
	@NotNull
	private String marca;
	
}
