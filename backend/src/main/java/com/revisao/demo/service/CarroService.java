package com.revisao.demo.service;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.dto.dashboards.CarOptionDTO;
import com.revisao.demo.enums.CarroStatus;
import com.revisao.demo.exception.NotFoundException;
import com.revisao.demo.mapper.CarroMapper;
import com.revisao.demo.models.Carro;
import com.revisao.demo.repository.AbastRepository;
import com.revisao.demo.repository.CarroRepository;
import com.revisao.demo.repository.MarcaRepository;
import com.revisao.demo.repository.ModeloRepository;
import com.revisao.demo.models.Marca;
import com.revisao.demo.models.Modelo;
import com.revisao.demo.repository.RevisaoRepository;

@Service
public class CarroService extends BaseServiceImpl<CarroDTO, Carro, Integer> {

    private final CarroRepository carroRepository;
    private final AbastRepository abastRepository;
    private final RevisaoRepository revisaoRepository;
    private final EmpresaService empresaService;
    private final MarcaRepository marcaRepository;
    private final ModeloRepository modeloRepository;

    public CarroService(CarroRepository carroRepository, CarroMapper carroMapper, AbastRepository abastRepository,
            RevisaoRepository revisaoRepository, EmpresaService empresaService,
            MarcaRepository marcaRepository, ModeloRepository modeloRepository) {
        super(carroRepository, carroMapper);
        this.carroRepository = carroRepository;
        this.abastRepository = abastRepository;
        this.revisaoRepository = revisaoRepository;
        this.empresaService = empresaService;
        this.marcaRepository = marcaRepository;
        this.modeloRepository = modeloRepository;
    }

    private Marca marcaDaEmpresa(Marca marca, Integer empresaId) {
        if (marca == null || marca.getId() == null) throw new IllegalArgumentException("Marca é obrigatória.");
        return marcaRepository.findByIdAndEmpresaId(marca.getId(), empresaId)
                .orElseThrow(() -> new NotFoundException("Marca não encontrada."));
    }

    private Modelo modeloDaEmpresa(Modelo modelo, Integer empresaId) {
        if (modelo == null || modelo.getId() == null) throw new IllegalArgumentException("Modelo é obrigatório.");
        return modeloRepository.findByIdAndEmpresaId(modelo.getId(), empresaId)
                .orElseThrow(() -> new NotFoundException("Modelo não encontrado."));
    }

    /** Busca o carro garantindo que ele pertence à empresa do usuário logado. */
    public Carro getCarroDaEmpresa(Integer carroId, Integer empresaId) {
        if (carroId == null) throw new IllegalArgumentException("Carro não informado.");
        return carroRepository.findByIdAndEmpresaId(carroId, empresaId)
                .orElseThrow(() -> new NotFoundException("Carro não encontrado."));
    }

    public CarroDTO findByIdDaEmpresa(Integer carroId, Integer empresaId) {
        return mapper.toDTO(getCarroDaEmpresa(carroId, empresaId));
    }

    public Double getKmRecente(Integer idCarro) {
        Double km = carroRepository.findKmMaisRecenteByIdCarro(idCarro);
        return km != null ? km : 0d;
    }

    public Double getKmRecente(Integer idCarro, Integer empresaId) {
        getCarroDaEmpresa(idCarro, empresaId);
        return getKmRecente(idCarro);
    }

    public void salvarCarro(CarroDTO carro, Integer empresaId) {
        if (carro.getPlaca() == null || carro.getPlaca().isBlank())
            throw new IllegalArgumentException("Placa é obrigatória.");
        carro.setPlaca(carro.getPlaca().trim().toLowerCase());
        if (carroRepository.existsByPlaca(carro.getPlaca()))
            throw new IllegalArgumentException("Carro com essa placa já existente!");
        carro.setId(null);
        carro.setMarca(marcaDaEmpresa(carro.getMarca(), empresaId));
        carro.setModelo(modeloDaEmpresa(carro.getModelo(), empresaId));
        carro.setEmpresa(empresaService.setEmpresaDTO(empresaId));
        carro.setDtAdicionado(new Date(System.currentTimeMillis()));
        if (carro.getStatus() == null) carro.setStatus(CarroStatus.DISPONIVEL);
        save(carro);
    }

    /**
     * Antes o update convertia o DTO sem id e acabava INSERINDO um carro novo
     * (e sem empresa). Agora atualiza apenas os campos editáveis do registro existente.
     */
    public CarroDTO atualizarCarro(Integer carroId, CarroDTO dados, Integer empresaId) {
        Carro carro = getCarroDaEmpresa(carroId, empresaId);
        if (dados.getPlaca() != null && !dados.getPlaca().isBlank()) {
            String placa = dados.getPlaca().trim().toLowerCase();
            if (!placa.equals(carro.getPlaca()) && carroRepository.existsByPlaca(placa))
                throw new IllegalArgumentException("Carro com essa placa já existente!");
            carro.setPlaca(placa);
        }
        if (dados.getMarca() != null) carro.setMarca(marcaDaEmpresa(dados.getMarca(), empresaId));
        if (dados.getModelo() != null) carro.setModelo(modeloDaEmpresa(dados.getModelo(), empresaId));
        if (dados.getAlarmeRevisaoKm() != null) carro.setAlarmeRevisaoKm(dados.getAlarmeRevisaoKm());
        if (dados.getAlarmeRevisaoMes() != null) carro.setAlarmeRevisaoMes(dados.getAlarmeRevisaoMes());
        if (dados.getStatus() != null) carro.setStatus(dados.getStatus());
        return mapper.toDTO(carroRepository.save(carro));
    }

    @Transactional
    public void deleteCarro(Integer carroId, Integer empresaId) {
        getCarroDaEmpresa(carroId, empresaId);
        abastRepository.deleteByCarro_id(carroId);
        revisaoRepository.deleteByCarro_id(carroId);
        delete(carroId);
    }

    public List<CarroDTO> getCarrosByEmpresa(Integer empresaId) {
        return mapper.toDTOList(carroRepository.findByEmpresaIdOrderByPlacaAsc(empresaId));
    }

    public List<CarOptionDTO> getCarOptions(Integer empresaId) {
        return carroRepository.findByEmpresaIdOrderByPlacaAsc(empresaId).stream()
                .map(c -> new CarOptionDTO(c.getId(), c.getPlaca(), getKmRecente(c.getId())))
                .toList();
    }

    public void updateCarroStatus(Integer carroId, CarroStatus status, Integer empresaId) {
        if (status == null) throw new IllegalArgumentException("Status inválido.");
        Carro carro = getCarroDaEmpresa(carroId, empresaId);
        carro.setStatus(status);
        carroRepository.save(carro);
    }
}
