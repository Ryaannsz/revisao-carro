package com.revisao.demo.service;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.MarcaDTO;
import com.revisao.demo.mapper.MarcaMapper;
import com.revisao.demo.models.Marca;
import com.revisao.demo.repository.MarcaRepository;

@Service
public class MarcaService extends BaseServiceImpl<MarcaDTO, Marca, Integer>{

	public MarcaService(MarcaRepository repository, MarcaMapper mapper) {
        super(repository, mapper);
    }
}
