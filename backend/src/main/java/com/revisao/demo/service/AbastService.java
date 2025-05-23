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
	
	private final CarroService carroService;
	
	public AbastService(AbastRepository abastRepository,
			AbastMapper abastMapper,
			CarroService carroService) {
		super(abastRepository, abastMapper);
		this.abastMapper=abastMapper;
		this.abastRepository=abastRepository;
		this.carroService=carroService;
		
	}
	
	public AbastDTO saveAbast(AbastDTO abast) {
		
		if(abast.getDtAbast()==null) {
			Timestamp agora = new Timestamp(System.currentTimeMillis());
			abast.setDtAbast(agora);
		}
		
		if(carroService.getKmRecente(abast.getCarro().getIdCarro())>=abast.getKmAtual()) {
			throw new IllegalArgumentException("Quilometragem inferior a atual.");
		}
		

		return save(abast);
	}
	
	public List<AbastDTO> findAllByCarroId(Integer idCarro){
		return abastMapper.toDTOList(abastRepository.findByCarroIdCarro(idCarro));
	}
	
}
