package com.revisao.demo.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="TB_USER")
public class User {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idUser;
	
	private String nome;
	
	private String cpf;
	
	private String senha;
	
	
}
