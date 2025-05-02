package com.revisao.demo.service;

import com.revisao.demo.dto.AbastDTO;
import com.revisao.demo.mapper.AbastMapper;
import com.revisao.demo.models.Abast;
import com.revisao.demo.repository.AbastRepository;

public class AbastService extends BaseServiceImpl<AbastDTO, Abast, Integer>{

	
	public AbastService(AbastRepository abastRepository, AbastMapper abastMapper) {
		super(abastRepository, abastMapper);
		
	}
}
