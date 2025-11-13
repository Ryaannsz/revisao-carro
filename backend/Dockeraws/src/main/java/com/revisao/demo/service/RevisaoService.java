package com.revisao.demo.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import com.revisao.demo.dto.dashboards.ProximaRevisaoDTO;
import com.revisao.demo.utils.AuthUtils;
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

    public List<ProximaRevisaoDTO> calcularProxRevisao(){
        List<Revisao> ultimasRevisoes = revisaoRepository.ultimaRevisaoPorCarro(AuthUtils.getEmpresaId());
        return ultimasRevisoes.stream().map( r -> {
            var kmRevisaoEstimado = r.getKmAtual()+r.getCarro().getAlarmeRevisaoKm();
            var proxRevisaoDate = addMonths(r.getDtRevisao(), r.getCarro().getAlarmeRevisaoMes());
            var placa = r.getCarro().getPlaca();
            var kmAtual = carroService.getKmRecente(r.getCarro().getId());
            return new ProximaRevisaoDTO(r.getCarro().getId(), placa, r.getDtRevisao(), kmRevisaoEstimado, kmAtual, proxRevisaoDate);
        }).toList();
    }

    public ProximaRevisaoDTO calcularProxRevisao(Integer carroId) {
        Revisao ultimaRevisao = revisaoRepository.findUltimaRevisaoByCarroId(carroId).orElseThrow();
        return new ProximaRevisaoDTO(ultimaRevisao.getCarro().getId(),
                ultimaRevisao.getCarro().getPlaca(), ultimaRevisao.getDtRevisao(),
                ultimaRevisao.getKmAtual()+ultimaRevisao.getCarro().getAlarmeRevisaoKm(),
                carroService.getKmRecente(ultimaRevisao.getCarro().getId()),
                addMonths(ultimaRevisao.getDtRevisao(), ultimaRevisao.getCarro().getAlarmeRevisaoMes()));
    }

    private Date addMonths(Date base, Integer months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.MONTH, months);
        return new Date(cal.getTimeInMillis());
    }

}
