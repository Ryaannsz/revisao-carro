package com.revisao.demo.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.dto.dashboards.ProximaRevisaoDTO;
import com.revisao.demo.exception.NotFoundException;
import com.revisao.demo.mapper.RevisaoMapper;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.Revisao;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.CarroRepository;
import com.revisao.demo.repository.RevisaoRepository;

@Service
public class RevisaoService extends BaseServiceImpl<RevisaoDTO, Revisao, Integer> {

    /** Valores usados quando o carro não tem intervalo de revisão configurado. */
    private static final double KM_PADRAO = 10_000d;
    private static final int MESES_PADRAO = 6;

    private final RevisaoRepository revisaoRepository;
    private final CarroRepository carroRepository;
    private final CarroService carroService;

    public RevisaoService(RevisaoRepository revisaoRepository, RevisaoMapper revisaoMapper,
            CarroService carroService, CarroRepository carroRepository) {
        super(revisaoRepository, revisaoMapper);
        this.revisaoRepository = revisaoRepository;
        this.carroService = carroService;
        this.carroRepository = carroRepository;
    }

    public RevisaoDTO saveRevisao(RevisaoDTO revisao, User usuarioLogado, Integer empresaId) {
        if (revisao.getCarro() == null) throw new IllegalArgumentException("Selecione o carro.");
        Carro carro = carroService.getCarroDaEmpresa(revisao.getCarro().getId(), empresaId);

        if (revisao.getDtRevisao() == null) {
            revisao.setDtRevisao(new Date(System.currentTimeMillis()));
        }

        // Revisão pode acontecer no mesmo km do último abastecimento; só não pode voltar o odômetro.
        Double kmRecente = carroService.getKmRecente(carro.getId());
        if (kmRecente != null && revisao.getKmAtual() < kmRecente) {
            throw new IllegalArgumentException("Quilometragem não pode ser menor que a atual (" + kmRecente.longValue() + " km).");
        }

        revisao.setId(null);
        revisao.setCarro(carro);
        revisao.setUser(usuarioLogado);
        return save(revisao);
    }

    public List<RevisaoDTO> findAllByEmpresa(Integer empresaId) {
        return mapper.toDTOList(revisaoRepository.findByCarro_Empresa_IdOrderByDtRevisaoDescIdDesc(empresaId));
    }

    public List<RevisaoDTO> listRevisaoByCarroId(Integer carroId, Integer empresaId) {
        carroService.getCarroDaEmpresa(carroId, empresaId);
        return mapper.toDTOList(revisaoRepository.findByCarro_IdOrderByDtRevisaoDescIdDesc(carroId));
    }

    private Revisao getDaEmpresa(Integer id, Integer empresaId) {
        return revisaoRepository.findByIdAndCarro_Empresa_Id(id, empresaId)
                .orElseThrow(() -> new NotFoundException("Revisão não encontrada."));
    }

    public RevisaoDTO findByIdDaEmpresa(Integer id, Integer empresaId) {
        return mapper.toDTO(getDaEmpresa(id, empresaId));
    }

    public void deleteDaEmpresa(Integer id, Integer empresaId) {
        revisaoRepository.delete(getDaEmpresa(id, empresaId));
    }

    /**
     * Próxima revisão de TODOS os carros da empresa. Carros que ainda não tiveram
     * revisão usam a data/km de cadastro como base (antes ficavam de fora da lista).
     */
    public List<ProximaRevisaoDTO> calcularProxRevisao(Integer empresaId) {
        Map<Integer, Revisao> ultimas = revisaoRepository.ultimaRevisaoPorCarro(empresaId).stream()
                .collect(Collectors.toMap(r -> r.getCarro().getId(), Function.identity(), (a, b) -> a));
        return carroRepository.findByEmpresaIdOrderByPlacaAsc(empresaId).stream()
                .map(c -> montarProxima(c, ultimas.get(c.getId())))
                .toList();
    }

    public ProximaRevisaoDTO calcularProxRevisao(Integer carroId, Integer empresaId) {
        Carro carro = carroService.getCarroDaEmpresa(carroId, empresaId);
        return montarProxima(carro, revisaoRepository.findUltimaRevisaoByCarroId(carroId).orElse(null));
    }

    private ProximaRevisaoDTO montarProxima(Carro carro, Revisao ultima) {
        double intervaloKm = carro.getAlarmeRevisaoKm() != null && carro.getAlarmeRevisaoKm() > 0
                ? carro.getAlarmeRevisaoKm() : KM_PADRAO;
        int intervaloMeses = carro.getAlarmeRevisaoMes() != null && carro.getAlarmeRevisaoMes() > 0
                ? carro.getAlarmeRevisaoMes() : MESES_PADRAO;

        Date baseData = ultima != null ? ultima.getDtRevisao() : carro.getDtAdicionado();
        Double baseKm = ultima != null ? ultima.getKmAtual() : carro.getKmAdicionado();

        Double kmEstimado = baseKm != null ? baseKm + intervaloKm : null;
        Date proxData = baseData != null ? addMonths(baseData, intervaloMeses) : null;

        return new ProximaRevisaoDTO(carro.getId(), carro.getPlaca(),
                ultima != null ? ultima.getDtRevisao() : null,
                kmEstimado, carroService.getKmRecente(carro.getId()), proxData);
    }

    private Date addMonths(Date base, int months) {
        LocalDate ld = base.toLocalDate();
        return Date.valueOf(ld.plusMonths(months));
    }
}
