package com.revisao.demo.service;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.mapper.RevisaoMapper;
import com.revisao.demo.models.Revisao;
import com.revisao.demo.repository.RevisaoRepository;

@Service
public class RevisaoService
     extends BaseServiceImpl<RevisaoDTO, Revisao, Integer> {
	
	



    @Autowired
    public RevisaoService(RevisaoRepository revisaoRepository,  
                          RevisaoMapper revisaoMapper) {
        super(revisaoRepository, revisaoMapper);
            
    }
    

    public RevisaoDTO saveRevisao(RevisaoDTO revisao) {
    	Timestamp agora = new Timestamp(System.currentTimeMillis());
    	revisao.setDtRevisao(agora);
    	return save(revisao);
    	
    }

}
