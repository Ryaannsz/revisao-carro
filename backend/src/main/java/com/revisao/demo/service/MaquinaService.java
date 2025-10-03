package com.revisao.demo.service;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.MaquinaDTO;
import com.revisao.demo.mapper.MaquinaMapper;
import com.revisao.demo.models.Maquina;
import com.revisao.demo.repository.MaquinaRepository;

@Service
public class MaquinaService extends BaseServiceImpl<MaquinaDTO, Maquina, Integer> {

    public MaquinaService(MaquinaRepository repository, MaquinaMapper mapper) {
	super(repository, mapper);
    }

}
