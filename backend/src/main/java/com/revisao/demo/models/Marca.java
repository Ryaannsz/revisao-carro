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
@Table(name="TB_MARCA")
public class Marca {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idMarca;
	
	private String marca; 

}
