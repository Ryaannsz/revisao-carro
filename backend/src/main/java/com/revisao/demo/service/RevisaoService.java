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

    private final RevisaoRepository revisaoRepository;

    @Autowired
    public RevisaoService(RevisaoRepository revisaoRepository,  
                          RevisaoMapper revisaoMapper) {
        super(revisaoRepository, revisaoMapper);
        this.revisaoRepository = revisaoRepository;             
    }
    

    public RevisaoDTO saveRevisao(RevisaoDTO revisao) {
    	Timestamp agora = new Timestamp(System.currentTimeMillis());
    	revisao.setDtRevisao(agora);
    	return save(revisao);
    	
    }

    public Double getKmRecente(Integer idCarro) {
        return revisaoRepository
            .findLatestKmAtualNative(idCarro)
            .orElse(null);
    }
}
