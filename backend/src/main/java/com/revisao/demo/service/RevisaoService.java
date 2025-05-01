package com.revisao.demo.service;

import org.springframework.stereotype.Service;


import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.mapper.RevisaoMapper;
import com.revisao.demo.models.Revisao;
import com.revisao.demo.repository.RevisaoRepository;

@Service
public class RevisaoService extends BaseServiceImpl<RevisaoDTO, Revisao, Integer>{

	public RevisaoService(RevisaoRepository revisaoRepositoy, RevisaoMapper revisaoMapper) {
		super(revisaoRepositoy, revisaoMapper);
	}
}
