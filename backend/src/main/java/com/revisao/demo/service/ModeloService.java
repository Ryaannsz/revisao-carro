package com.revisao.demo.service;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.ModeloDTO;
import com.revisao.demo.mapper.ModeloMapper;
import com.revisao.demo.models.Modelo;
import com.revisao.demo.repository.ModeloRepository;

@Service
public class ModeloService extends BaseServiceImpl<ModeloDTO, Modelo, Integer>{
	
	private ModeloRepository modeloRepository;
	
	private ModeloMapper modeloMapper;
	
	public ModeloService(ModeloRepository modeloRepository, ModeloMapper modeloMapper) {
		super(modeloRepository, modeloMapper);
		this.modeloRepository = modeloRepository;
		this.modeloMapper = modeloMapper;
	}
	
	public ModeloDTO salvarModelo(ModeloDTO modelo) {
		if (modeloRepository.existsByModelo(modelo.getModelo().toLowerCase()))
			throw new IllegalArgumentException("Modelo já adicionado!");
		modelo.setModelo(modelo.getModelo().toLowerCase());
		return modeloMapper.toDTO(modeloRepository.saveAndFlush(modeloMapper.toEntity(modelo)));
	}

}
