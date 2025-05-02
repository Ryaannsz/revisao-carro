package com.revisao.demo.service;

import java.sql.Timestamp;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.AbastDTO;
import com.revisao.demo.mapper.AbastMapper;
import com.revisao.demo.models.Abast;
import com.revisao.demo.repository.AbastRepository;

@Service
public class AbastService extends BaseServiceImpl<AbastDTO, Abast, Integer>{

	
	public AbastService(AbastRepository abastRepository, AbastMapper abastMapper) {
		super(abastRepository, abastMapper);
		
	}
	
	public AbastDTO saveAbast(AbastDTO abast) {
		if(abast.getDtAbast()==null) {
			Timestamp agora = new Timestamp(System.currentTimeMillis());
			abast.setDtAbast(agora);
		}
		return save(abast);
	}
}
