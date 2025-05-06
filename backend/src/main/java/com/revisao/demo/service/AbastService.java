package com.revisao.demo.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.AbastDTO;
import com.revisao.demo.mapper.AbastMapper;
import com.revisao.demo.models.Abast;
import com.revisao.demo.repository.AbastRepository;

@Service
public class AbastService extends BaseServiceImpl<AbastDTO, Abast, Integer>{

	private final AbastRepository abastRepository;
	private final AbastMapper abastMapper;
	
	public AbastService(AbastRepository abastRepository, AbastMapper abastMapper) {
		super(abastRepository, abastMapper);
		this.abastMapper=abastMapper;
		this.abastRepository=abastRepository;
		
	}
	
	public AbastDTO saveAbast(AbastDTO abast) {
		System.out.println(abast);
		if(abast.getDtAbast()==null) {
			Timestamp agora = new Timestamp(System.currentTimeMillis());
			abast.setDtAbast(agora);
		}
		return save(abast);
	}
	
	public List<AbastDTO> findAllByCarroId(Integer idCarro){
		return abastMapper.toDTOList(abastRepository.findByCarroIdCarro(idCarro));
	}
	
}
