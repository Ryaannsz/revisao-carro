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


    @Autowired
    public RevisaoService(RevisaoRepository revisaoRepository,  
                          RevisaoMapper revisaoMapper) {
        super(revisaoRepository, revisaoMapper);
        this.revisaoRepository=revisaoRepository;
        this.revisaoMapper=revisaoMapper;
            
    }
    

    public RevisaoDTO saveRevisao(RevisaoDTO revisao) {
    	Timestamp agora = new Timestamp(System.currentTimeMillis());
    	revisao.setDtRevisao(agora);
    	return save(revisao);
    	
    }
    
    public List<RevisaoDTO> listRevisaoByCarroId(Integer idCarro){
    	return revisaoMapper.toDTOList(revisaoRepository.findByCarroIdCarro(idCarro));
    }

}
