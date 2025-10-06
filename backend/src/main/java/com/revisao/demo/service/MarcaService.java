package com.revisao.demo.service;

import com.revisao.demo.dto.EmpresaDTO;
import com.revisao.demo.dto.ModeloDTO;
import com.revisao.demo.mapper.EmpresaMapper;
import com.revisao.demo.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.revisao.demo.dto.MarcaDTO;
import com.revisao.demo.mapper.MarcaMapper;
import com.revisao.demo.models.Marca;
import com.revisao.demo.repository.MarcaRepository;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class MarcaService extends BaseServiceImpl<MarcaDTO, Marca, Integer>{

	private final MarcaRepository marcaRepository;
	
	private final MarcaMapper marcaMapper;

    private final EmpresaService empresaService;


	
	public MarcaService(MarcaRepository repository, MarcaMapper mapper, EmpresaService empresaService) {
        super(repository, mapper);
        this.marcaRepository=repository;
        this.marcaMapper=mapper;
        this.empresaService=empresaService;
    }
	
	public MarcaDTO salvarMarca(MarcaDTO marca, Integer empresaId) {
		if (marcaRepository.existsByMarca(marca.getMarca().toLowerCase()))
			throw new IllegalArgumentException("Marca já adicionada!");
		marca.setMarca(marca.getMarca().toLowerCase());
        marca.setEmpresa(empresaService.setEmpresaDTO(empresaId));
		return marcaMapper.toDTO(marcaRepository.saveAndFlush(marcaMapper.toEntity(marca)));
		}

    public List<MarcaDTO> findAllByEmpresaId(Integer empresaId) {
        return marcaMapper.toDTOList(marcaRepository.findAllByEmpresaIdOrderByMarcaAsc(empresaId));
    }
	
}
