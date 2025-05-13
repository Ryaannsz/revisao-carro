package com.revisao.demo.models;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_CARRO")


public class Carro {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idCarro;
	
	private Timestamp dtAdicionado;
	
	private Double kmAdicionado;
	
	private String placa;
	
	
	@ManyToOne
	@JoinColumn(name="modelo_id")
	private Modelo modelo;
	
	
	
	@ManyToOne
	@JoinColumn(name="marca_id")
	private Marca marca;
	

}
