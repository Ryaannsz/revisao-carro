package com.revisao.demo.service;

import com.revisao.demo.dto.DashboardResumoDTO;
import com.revisao.demo.dto.MesValorDTO;
import com.revisao.demo.dto.ProximaRevisaoDTO;
import com.revisao.demo.models.Revisao;
import com.revisao.demo.repository.AbastRepository;
import com.revisao.demo.repository.RevisaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.logging.Logger;

import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DashboardService {

    private final AbastRepository abastRepository;
    private final RevisaoRepository revisaoRepository;

    private final CarroService carroService;

    public DashboardResumoDTO resumoDaEmpresa(String empresaId) {
        var gasto = abastRepository.gastoMensal(empresaId);
        var litros = abastRepository.litrosMensais(empresaId);
        var km = abastRepository.kmRodadoMensal(empresaId);

        var preco = abastRepository.precoMedioLitroMensalRaw(empresaId)
                .stream()
                .map(row -> new MesValorDTO(
                        (String) row[0],
                        row[1] != null ? ((Number) row[1]).doubleValue() : null
                ))
                .toList();;
        var revisoes = revisaoRepository.revisoesPorMes(empresaId);


        var eficiencia = calcularEficiencia(km, litros);

        var proximas = estimarProximasRevisoes(revisaoRepository.ultimaRevisaoPorCarro(empresaId));


        var dto = new DashboardResumoDTO();
        dto.setGastoMensal(gasto);
        dto.setLitrosMensais(litros);
        dto.setKmRodadoMensal(km);
        dto.setPrecoMedioLitroMensal(preco);
        dto.setEficienciaMensal(eficiencia);
        dto.setRevisoesPorMes(revisoes);
        dto.setProximasRevisoes(proximas);
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

    private List<ProximaRevisaoDTO> estimarProximasRevisoes(List<Revisao> ultimas) {
        // Exemplo: próxima revisão = última revisão + 10.000 km (ajuste para sua regra real)

        return ultimas.stream().map(r -> {
            var kmRecente = carroService.getKmRecente((r.getCarro().getId()));
            var carro = r.getCarro();
            var placa = carro.getPlaca();
            var prox = plusDaysDate(r.getDtRevisao(), 180);
            java.sql.Date sqlDate = new java.sql.Date(prox.getTime());
            return new ProximaRevisaoDTO(carro.getId(), placa, r.getDtRevisao(), kmRecente, sqlDate);
        }).toList();
    }

    public static java.util.Date plusDaysDate(java.util.Date in, long days) {
        Objects.requireNonNull(in, "Data não pode ser nula");

        if (in instanceof java.sql.Date sqlDate) {
            LocalDate base = sqlDate.toLocalDate().plusDays(days);
            return java.util.Date.from(base.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else {
            return java.util.Date.from(in.toInstant().plusSeconds(days * 24 * 60 * 60));
        }
    }

}
