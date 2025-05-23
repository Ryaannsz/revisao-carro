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
@Table(name="tb_abastecimento")
public class Abast {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idAbast;
	
	private Double litroComb;
	
	private Double valorComb;
	
	private Timestamp dtAbast;
	
	private Double kmAtual;
	

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	


	
	@ManyToOne
	@JoinColumn(name="carro_id")
	private Carro carro;

}
