package com.revisao.demo.service;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.ModeloDTO;
import com.revisao.demo.mapper.ModeloMapper;
import com.revisao.demo.models.Modelo;
import com.revisao.demo.repository.ModeloRepository;

@Service
public class ModeloService extends BaseServiceImpl<ModeloDTO, Modelo, Integer>{
	
	public ModeloService(ModeloRepository modeloRepository, ModeloMapper modeloMapper) {
		super(modeloRepository, modeloMapper);
	}

}
