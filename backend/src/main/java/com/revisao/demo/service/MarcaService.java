package com.revisao.demo.service;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.MarcaDTO;
import com.revisao.demo.mapper.MarcaMapper;
import com.revisao.demo.models.Marca;
import com.revisao.demo.repository.MarcaRepository;

@Service
public class MarcaService extends BaseServiceImpl<MarcaDTO, Marca, Integer>{

	private MarcaRepository marcaRepository;
	
	public MarcaService(MarcaRepository repository, MarcaMapper mapper) {
        super(repository, mapper);
        this.marcaRepository=repository;
    }
	
	public void salvarMarca(MarcaDTO marca) {
		if (marcaRepository.existsByMarca(marca.getMarca().toLowerCase()))
			throw new IllegalArgumentException("Marca já adicionada!");
		marca.setMarca(marca.getMarca().toLowerCase());
		save(marca);
	}
	
}
