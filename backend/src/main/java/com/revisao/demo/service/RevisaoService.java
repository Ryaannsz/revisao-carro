package com.revisao.demo.service;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.mapper.RevisaoMapper;
import com.revisao.demo.models.Revisao;
import com.revisao.demo.repository.RevisaoRepository;

@Service
public class RevisaoService extends BaseServiceImpl<RevisaoDTO, Revisao, Integer> {

    private final RevisaoRepository revisaoRepository;
    private final RevisaoMapper revisaoMapper;

    private final CarroService carroService;

    public RevisaoService(RevisaoRepository revisaoRepository, RevisaoMapper revisaoMapper, CarroService carroService) {
	super(revisaoRepository, revisaoMapper);
	this.revisaoRepository = revisaoRepository;
	this.revisaoMapper = revisaoMapper;
	this.carroService = carroService;
    }

    public RevisaoDTO saveRevisao(RevisaoDTO revisao) {

	if (revisao.getDtRevisao() == null) {
	    Date agora = new Date(System.currentTimeMillis());
	    revisao.setDtRevisao(agora);
	}

	if (carroService.getKmRecente(revisao.getCarro().getId()) >= revisao.getKmAtual()) {
	    throw new IllegalArgumentException("Quilometragem inferior a atual.");
	}

	return save(revisao);

    }

    public List<RevisaoDTO> listRevisaoByCarroId(Integer carroId) {
	return revisaoMapper.toDTOList(revisaoRepository.findByCarro_id(carroId));
    }

}
