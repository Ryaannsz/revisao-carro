package com.revisao.demo.dto;

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
	@NotNull
	private Integer idUser;

	private User user;

}
