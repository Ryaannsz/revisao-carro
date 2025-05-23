package com.revisao.demo.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.mapper.RevisaoMapper;
import com.revisao.demo.models.Revisao;
import com.revisao.demo.repository.RevisaoRepository;

@Service
public class RevisaoService
     extends BaseServiceImpl<RevisaoDTO, Revisao, Integer> {
	
	
	private final RevisaoRepository revisaoRepository;
	private final RevisaoMapper revisaoMapper;
	
	private final CarroService carroService;



    @Autowired
    public RevisaoService(RevisaoRepository revisaoRepository,  
                          RevisaoMapper revisaoMapper,
                          CarroService carroService) {
        super(revisaoRepository, revisaoMapper);
        this.revisaoRepository=revisaoRepository;
        this.revisaoMapper=revisaoMapper;
        this.carroService=carroService;            
    }
    

    public RevisaoDTO saveRevisao(RevisaoDTO revisao) {
    	
		if(revisao.getDtRevisao()==null) {
	    	Timestamp agora = new Timestamp(System.currentTimeMillis());
	    	revisao.setDtRevisao(agora);
		}
    	
		if(carroService.getKmRecente(revisao.getCarro().getIdCarro())>=revisao.getKmAtual()) {
			throw new IllegalArgumentException("Quilometragem inferior a atual.");
		}


    	return save(revisao);
    	
    }
    
    public List<RevisaoDTO> listRevisaoByCarroId(Integer idCarro){
    	return revisaoMapper.toDTOList(revisaoRepository.findByCarroIdCarro(idCarro));
    }

}
