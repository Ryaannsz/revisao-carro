package com.revisao.demo.service;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.AbastDTO;
import com.revisao.demo.exception.NotFoundException;
import com.revisao.demo.mapper.AbastMapper;
import com.revisao.demo.models.Abast;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.AbastRepository;

@Service
public class AbastService extends BaseServiceImpl<AbastDTO, Abast, Integer> {

    private final AbastRepository abastRepository;
    private final CarroService carroService;

    public AbastService(AbastRepository abastRepository, AbastMapper abastMapper, CarroService carroService) {
        super(abastRepository, abastMapper);
        this.abastRepository = abastRepository;
        this.carroService = carroService;
    }

    public AbastDTO saveAbast(AbastDTO abast, User usuarioLogado, Integer empresaId) {
        if (abast.getCarro() == null) throw new IllegalArgumentException("Selecione o carro.");
        Carro carro = carroService.getCarroDaEmpresa(abast.getCarro().getId(), empresaId);

        if (abast.getDtAbast() == null) {
            abast.setDtAbast(new Date(System.currentTimeMillis()));
        }

        Double kmRecente = carroService.getKmRecente(carro.getId());
        if (kmRecente != null && abast.getKmAtual() <= kmRecente) {
            throw new IllegalArgumentException("Quilometragem deve ser maior que a atual (" + kmRecente.longValue() + " km).");
        }

        abast.setId(null);
        abast.setCarro(carro);
        // quem registrou vem do token, não do corpo da requisição
        abast.setUser(usuarioLogado);
        return save(abast);
    }

    public List<AbastDTO> findAllByEmpresa(Integer empresaId) {
        return mapper.toDTOList(abastRepository.findByCarro_Empresa_IdOrderByDtAbastDescIdDesc(empresaId));
    }

    public List<AbastDTO> findAllByCarroId(Integer carroId, Integer empresaId) {
        carroService.getCarroDaEmpresa(carroId, empresaId);
        return mapper.toDTOList(abastRepository.findByCarro_IdOrderByDtAbastDescIdDesc(carroId));
    }

    private Abast getDaEmpresa(Integer id, Integer empresaId) {
        return abastRepository.findByIdAndCarro_Empresa_Id(id, empresaId)
                .orElseThrow(() -> new NotFoundException("Abastecimento não encontrado."));
    }

    public AbastDTO findByIdDaEmpresa(Integer id, Integer empresaId) {
        return mapper.toDTO(getDaEmpresa(id, empresaId));
    }

    public void deleteDaEmpresa(Integer id, Integer empresaId) {
        abastRepository.delete(getDaEmpresa(id, empresaId));
    }
}
