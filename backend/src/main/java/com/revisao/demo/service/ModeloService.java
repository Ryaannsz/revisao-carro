package com.revisao.demo.service;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.ModeloDTO;
import com.revisao.demo.mapper.ModeloMapper;
import com.revisao.demo.models.Modelo;
import com.revisao.demo.repository.ModeloRepository;

import java.util.List;

@Service
public class ModeloService extends BaseServiceImpl<ModeloDTO, Modelo, Integer>{
	
	private ModeloRepository modeloRepository;
	
	private ModeloMapper modeloMapper;

    private EmpresaService empresaService;
	
	public ModeloService(ModeloRepository modeloRepository, ModeloMapper modeloMapper, EmpresaService empresaService) {
		super(modeloRepository, modeloMapper);
		this.modeloRepository = modeloRepository;
		this.modeloMapper = modeloMapper;
        this.empresaService = empresaService;
	}
	
	public ModeloDTO salvarModelo(ModeloDTO modelo, Integer empresaId) {
		if (modeloRepository.existsByModelo(modelo.getModelo().toLowerCase()))
			throw new IllegalArgumentException("Modelo já adicionado!");
		modelo.setModelo(modelo.getModelo().toLowerCase());
        modelo.setEmpresa(empresaService.setEmpresaDTO(empresaId));
		return modeloMapper.toDTO(modeloRepository.saveAndFlush(modeloMapper.toEntity(modelo)));
	}

    public List<ModeloDTO> findAllByEmpresaId(Integer empresaId) {
        return modeloMapper.toDTOList(modeloRepository.findAllByEmpresaIdOrderByModeloAsc(empresaId));
    }

}
