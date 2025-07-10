package com.revisao.demo.service;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.RevisaoMaquinaDTO;
import com.revisao.demo.mapper.RevisaoMaquinaMapper;
import com.revisao.demo.models.RevisaoMaquina;
import com.revisao.demo.repository.RevisaoMaquinaRepository;

@Service
public class RevisaoMaquinaService extends BaseServiceImpl<RevisaoMaquinaDTO, RevisaoMaquina, Integer> {

    public RevisaoMaquinaService(RevisaoMaquinaRepository repository, RevisaoMaquinaMapper mapper) {
	super(repository, mapper);
    }

}
