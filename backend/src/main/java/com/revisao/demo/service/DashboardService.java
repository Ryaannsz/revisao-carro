package com.revisao.demo.service;

import com.revisao.demo.dto.dashboards.DashboardResumoDTO;
import com.revisao.demo.dto.dashboards.MesValorDTO;
import com.revisao.demo.dto.dashboards.ProximaRevisaoDTO;
import com.revisao.demo.models.Revisao;
import com.revisao.demo.repository.AbastRepository;
import com.revisao.demo.repository.RevisaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DashboardService {

    private final AbastRepository abastRepository;
    private final RevisaoRepository revisaoRepository;

    private final RevisaoService revisaoService;


    public DashboardResumoDTO resumoDaEmpresa(Integer empresaId) {
        var gasto = abastRepository.gastoMensal(empresaId);
        var litros = abastRepository.litrosMensais(empresaId);
        var km = abastRepository.kmRodadoMensal(empresaId);

        var preco = abastRepository.precoMedioLitroMensalRaw(empresaId)
                .stream()
                .map(row -> new MesValorDTO(
                        (String) row[0],
                        row[1] != null ? ((Number) row[1]).doubleValue() : null
                ))
                .toList();
        var revisoes = revisaoRepository.revisoesPorMes(empresaId);


        var eficiencia = calcularEficiencia(km, litros);

        var proximas = revisaoService.calcularProxRevisao(empresaId);
        var custoManutencao = revisaoRepository.custoManutencaoMensal(empresaId);


        var dto = new DashboardResumoDTO();
        dto.setGastoMensal(gasto);
        dto.setLitrosMensais(litros);
        dto.setKmRodadoMensal(km);
        dto.setPrecoMedioLitroMensal(preco);
        dto.setEficienciaMensal(eficiencia);
        dto.setRevisoesPorMes(revisoes);
        dto.setProximasRevisoes(proximas);
        dto.setCustoManutencaoMensal(custoManutencao);
        return dto;
    }

    private List<MesValorDTO> calcularEficiencia(List<MesValorDTO> km, List<MesValorDTO> litros){
        var mapLitros = litros.stream().collect(Collectors.toMap(MesValorDTO::getMes, MesValorDTO::getValor));
        return km.stream().map(k -> {
            var l = mapLitros.getOrDefault(k.getMes(), 0d);
            var val = (l == 0) ? null : k.getValor() / l;
            return new MesValorDTO(k.getMes(), val);
        }).toList();
    }





}
