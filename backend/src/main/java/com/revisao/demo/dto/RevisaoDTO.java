package com.revisao.demo.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevisaoDTO {

	private Integer idRevisao;
	
	private Timestamp dtRevisao;
	
	
}
