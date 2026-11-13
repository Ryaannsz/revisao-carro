package com.revisao.demo.service;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revisao.demo.dto.LeituraHorimetroDTO;
import com.revisao.demo.dto.MaquinaDTO;
import com.revisao.demo.enums.MaquinaStatus;
import com.revisao.demo.exception.NotFoundException;
import com.revisao.demo.mapper.MaquinaMapper;
import com.revisao.demo.models.LeituraHorimetro;
import com.revisao.demo.models.Maquina;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.LeituraHorimetroRepository;
import com.revisao.demo.repository.MaquinaRepository;
import com.revisao.demo.repository.MarcaRepository;
import com.revisao.demo.repository.ModeloRepository;
import com.revisao.demo.repository.RevisaoMaquinaRepository;

@Service
public class MaquinaService extends BaseServiceImpl<MaquinaDTO, Maquina, Integer> {

    private final MaquinaRepository maquinaRepository;
    private final RevisaoMaquinaRepository revisaoMaquinaRepository;
    private final MarcaRepository marcaRepository;
    private final ModeloRepository modeloRepository;
    private final EmpresaService empresaService;
    private final LeituraHorimetroRepository leituraRepository;

    public MaquinaService(MaquinaRepository repository, MaquinaMapper mapper,
            RevisaoMaquinaRepository revisaoMaquinaRepository, MarcaRepository marcaRepository,
            ModeloRepository modeloRepository, EmpresaService empresaService,
            LeituraHorimetroRepository leituraRepository) {
        super(repository, mapper);
        this.maquinaRepository = repository;
        this.revisaoMaquinaRepository = revisaoMaquinaRepository;
        this.marcaRepository = marcaRepository;
        this.modeloRepository = modeloRepository;
        this.empresaService = empresaService;
        this.leituraRepository = leituraRepository;
    }

    public Maquina getMaquinaDaEmpresa(Integer id, Integer empresaId) {
        if (id == null) throw new IllegalArgumentException("Máquina não informada.");
        return maquinaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new NotFoundException("Máquina não encontrada."));
    }

    public MaquinaDTO findByIdDaEmpresa(Integer id, Integer empresaId) {
        return mapper.toDTO(getMaquinaDaEmpresa(id, empresaId));
    }

    public List<MaquinaDTO> findAllByEmpresa(Integer empresaId) {
        return mapper.toDTOList(maquinaRepository.findByEmpresaIdOrderByIdAsc(empresaId));
    }

    public Double getHorasRecentes(Integer maquinaId, Integer empresaId) {
        getMaquinaDaEmpresa(maquinaId, empresaId);
        Double h = maquinaRepository.findHorasMaisRecente(maquinaId);
        return h != null ? h : 0d;
    }

    public LeituraHorimetroDTO registrarLeitura(Integer maquinaId, LeituraHorimetroDTO dto, User usuario, Integer empresaId) {
        Maquina m = getMaquinaDaEmpresa(maquinaId, empresaId);
        Double atual = maquinaRepository.findHorasMaisRecente(m.getId());
        if (atual != null && dto.getHoras() < atual)
            throw new IllegalArgumentException("Horímetro não pode ser menor que o atual (" + atual + " h).");
        LeituraHorimetro l = new LeituraHorimetro(null,
                dto.getDtLeitura() != null ? dto.getDtLeitura() : new Date(System.currentTimeMillis()),
                dto.getHoras(), m, usuario);
        return toLeituraDTO(leituraRepository.save(l));
    }

    public List<LeituraHorimetroDTO> listarLeituras(Integer maquinaId, Integer empresaId) {
        getMaquinaDaEmpresa(maquinaId, empresaId);
        return leituraRepository.findByMaquina_IdOrderByDtLeituraDescIdDesc(maquinaId).stream()
                .map(this::toLeituraDTO).toList();
    }

    private LeituraHorimetroDTO toLeituraDTO(LeituraHorimetro l) {
        return new LeituraHorimetroDTO(l.getId(), l.getDtLeitura(), l.getHoras(),
                l.getUser() != null ? l.getUser().getNome() : null);
    }

    public MaquinaDTO salvarMaquina(MaquinaDTO dto, Integer empresaId) {
        if (dto.getChassi() != null && !dto.getChassi().isBlank()) {
            dto.setChassi(dto.getChassi().trim().toUpperCase());
            if (maquinaRepository.existsByChassiIgnoreCaseAndEmpresaId(dto.getChassi(), empresaId))
                throw new IllegalArgumentException("Já existe máquina com esse chassi.");
        }
        validarMarcaModelo(dto, empresaId);
        dto.setId(null);
        if (dto.getStatus() == null) dto.setStatus(MaquinaStatus.DISPONIVEL);
        dto.setEmpresa(empresaService.setEmpresaDTO(empresaId));
        if (dto.getDtAdicionado() == null) dto.setDtAdicionado(new Date(System.currentTimeMillis()));
        return save(dto);
    }

    /** Atualiza só os campos editáveis; empresa, data de cadastro e horímetro inicial não mudam. */
    public MaquinaDTO atualizarMaquina(Integer id, MaquinaDTO dados, Integer empresaId) {
        Maquina m = getMaquinaDaEmpresa(id, empresaId);
        if (dados.getChassi() != null) {
            String chassi = dados.getChassi().trim().toUpperCase();
            if (!chassi.isEmpty() && !chassi.equalsIgnoreCase(m.getChassi())
                    && maquinaRepository.existsByChassiIgnoreCaseAndEmpresaId(chassi, empresaId))
                throw new IllegalArgumentException("Já existe máquina com esse chassi.");
            m.setChassi(chassi.isEmpty() ? null : chassi);
        }
        if (dados.getIdentificacao() != null) m.setIdentificacao(dados.getIdentificacao().trim());
        if (dados.getTipo() != null) m.setTipo(dados.getTipo().trim());
        if (dados.getAno() != null) m.setAno(dados.getAno());
        if (dados.getAlarmeRevisaoHoras() != null) m.setAlarmeRevisaoHoras(dados.getAlarmeRevisaoHoras());
        if (dados.getAlarmeRevisaoMes() != null) m.setAlarmeRevisaoMes(dados.getAlarmeRevisaoMes());
        if (dados.getStatus() != null) m.setStatus(dados.getStatus());
        if (dados.getMarca() != null || dados.getModelo() != null) {
            validarMarcaModelo(dados, empresaId);
            if (dados.getMarca() != null) m.setMarca(dados.getMarca());
            if (dados.getModelo() != null) m.setModelo(dados.getModelo());
        }
        return mapper.toDTO(maquinaRepository.save(m));
    }

    public void atualizarStatus(Integer id, MaquinaStatus status, Integer empresaId) {
        if (status == null) throw new IllegalArgumentException("Status inválido.");
        Maquina m = getMaquinaDaEmpresa(id, empresaId);
        m.setStatus(status);
        maquinaRepository.save(m);
    }

    @Transactional
    public void deleteDaEmpresa(Integer id, Integer empresaId) {
        Maquina m = getMaquinaDaEmpresa(id, empresaId);
        revisaoMaquinaRepository.deleteByMaquina_Id(m.getId());
        leituraRepository.deleteByMaquina_Id(m.getId());
        maquinaRepository.delete(m);
    }

    private void validarMarcaModelo(MaquinaDTO dto, Integer empresaId) {
        if (dto.getMarca() != null && dto.getMarca().getId() != null)
            dto.setMarca(marcaRepository.findByIdAndEmpresaId(dto.getMarca().getId(), empresaId)
                    .orElseThrow(() -> new NotFoundException("Marca não encontrada.")));
        else dto.setMarca(null);
        if (dto.getModelo() != null && dto.getModelo().getId() != null)
            dto.setModelo(modeloRepository.findByIdAndEmpresaId(dto.getModelo().getId(), empresaId)
                    .orElseThrow(() -> new NotFoundException("Modelo não encontrado.")));
        else dto.setModelo(null);
    }
}
