package com.revisao.demo.service;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.RevisaoMaquinaDTO;
import com.revisao.demo.dto.dashboards.ProximaRevisaoMaquinaDTO;
import com.revisao.demo.exception.NotFoundException;
import com.revisao.demo.mapper.RevisaoMaquinaMapper;
import com.revisao.demo.models.Maquina;
import com.revisao.demo.models.RevisaoMaquina;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.MaquinaRepository;
import com.revisao.demo.repository.RevisaoMaquinaRepository;

@Service
public class RevisaoMaquinaService extends BaseServiceImpl<RevisaoMaquinaDTO, RevisaoMaquina, Integer> {

    private final RevisaoMaquinaRepository revisaoMaquinaRepository;
    private final MaquinaRepository maquinaRepository;
    private final MaquinaService maquinaService;

    public RevisaoMaquinaService(RevisaoMaquinaRepository repository, RevisaoMaquinaMapper mapper,
            MaquinaRepository maquinaRepository, MaquinaService maquinaService) {
        super(repository, mapper);
        this.revisaoMaquinaRepository = repository;
        this.maquinaRepository = maquinaRepository;
        this.maquinaService = maquinaService;
    }

    public RevisaoMaquinaDTO salvar(RevisaoMaquinaDTO dto, User usuarioLogado, Integer empresaId) {
        if (dto.getMaquina() == null) throw new IllegalArgumentException("Selecione a máquina.");
        Maquina maquina = maquinaService.getMaquinaDaEmpresa(dto.getMaquina().getId(), empresaId);

        Double horasRecentes = maquinaRepository.findHorasMaisRecente(maquina.getId());
        if (horasRecentes != null && dto.getHorasAtual() < horasRecentes)
            throw new IllegalArgumentException("Horímetro não pode ser menor que o atual (" + horasRecentes + " h).");

        if (dto.getDtRevisao() == null) dto.setDtRevisao(new Date(System.currentTimeMillis()));
        dto.setId(null);
        dto.setMaquina(maquina);
        dto.setUser(usuarioLogado);
        return save(dto);
    }

    public List<RevisaoMaquinaDTO> findAllByEmpresa(Integer empresaId) {
        return mapper.toDTOList(revisaoMaquinaRepository.findByMaquina_Empresa_IdOrderByDtRevisaoDescIdDesc(empresaId));
    }

    public List<RevisaoMaquinaDTO> findAllByMaquina(Integer maquinaId, Integer empresaId) {
        maquinaService.getMaquinaDaEmpresa(maquinaId, empresaId);
        return mapper.toDTOList(revisaoMaquinaRepository.findByMaquina_IdOrderByDtRevisaoDescIdDesc(maquinaId));
    }

    /** Intervalos usados quando a máquina não tem plano configurado (troca de óleo do motor típica). */
    private static final double HORAS_PADRAO = 250d;
    private static final int MESES_PADRAO = 6;

    public List<ProximaRevisaoMaquinaDTO> calcularProxRevisao(Integer empresaId) {
        return maquinaRepository.findByEmpresaIdOrderByIdAsc(empresaId).stream()
                .map(this::montarProxima)
                .toList();
    }

    public ProximaRevisaoMaquinaDTO calcularProxRevisao(Integer maquinaId, Integer empresaId) {
        return montarProxima(maquinaService.getMaquinaDaEmpresa(maquinaId, empresaId));
    }

    /**
     * Próxima preventiva = última manutenção (ou cadastro) + intervalo em horas,
     * e data da última + intervalo em meses. Vence pelo que chegar primeiro.
     */
    private ProximaRevisaoMaquinaDTO montarProxima(Maquina m) {
        double intervaloHoras = m.getAlarmeRevisaoHoras() != null && m.getAlarmeRevisaoHoras() > 0
                ? m.getAlarmeRevisaoHoras() : HORAS_PADRAO;
        int intervaloMeses = m.getAlarmeRevisaoMes() != null && m.getAlarmeRevisaoMes() > 0
                ? m.getAlarmeRevisaoMes() : MESES_PADRAO;

        RevisaoMaquina ultima = revisaoMaquinaRepository.findFirstByMaquina_IdOrderByDtRevisaoDescIdDesc(m.getId()).orElse(null);
        Double baseHoras = ultima != null ? ultima.getHorasAtual() : m.getHoras();
        Date baseData = ultima != null ? ultima.getDtRevisao() : m.getDtAdicionado();

        Double horasAtual = maquinaRepository.findHorasMaisRecente(m.getId());
        return new ProximaRevisaoMaquinaDTO(
                m.getId(),
                m.getIdentificacao(),
                ultima != null ? ultima.getDtRevisao() : null,
                baseHoras != null ? baseHoras + intervaloHoras : null,
                horasAtual != null ? horasAtual : 0d,
                baseData != null ? Date.valueOf(baseData.toLocalDate().plusMonths(intervaloMeses)) : null,
                intervaloHoras);
    }

    public void deleteDaEmpresa(Integer id, Integer empresaId) {
        revisaoMaquinaRepository.delete(revisaoMaquinaRepository.findByIdAndMaquina_Empresa_Id(id, empresaId)
                .orElseThrow(() -> new NotFoundException("Revisão não encontrada.")));
    }
}
