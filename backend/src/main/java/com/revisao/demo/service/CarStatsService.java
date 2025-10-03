package com.revisao.demo.service;


import com.revisao.demo.dto.dashboards.*;
import com.revisao.demo.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarStatsService {

    private final DashboardRepository repo;
    private final CarroService carroService; // <-- para getKmRecente(idCarro)

    public CarStatsResponseDTO getCarStats(List<Integer> carIds, Integer months, String mode, Integer empresaId) {
        Date start = null, end = null;
        if (months != null && months > 0) {
            YearMonth ymEnd = YearMonth.now().plusMonths(1); // exclusivo
            YearMonth ymStart = YearMonth.now().minusMonths(months - 1);
            start = Date.valueOf(LocalDate.of(ymStart.getYear(), ymStart.getMonth(), 1));
            end   = Date.valueOf(LocalDate.of(ymEnd.getYear(),  ymEnd.getMonth(),  1));
        }

        var rowsRaw = repo.carMonthAgg(carIds, start, end, empresaId);
        var agg = rowsRaw.stream().map(r -> new CarMonthAggRowDTO(
                ((Number) r[0]).intValue(),
                Objects.toString(r[1], null),
                Objects.toString(r[2], null),
                r[3] == null ? null : ((Number) r[3]).doubleValue(),
                r[4] == null ? null : ((Number) r[4]).doubleValue(),
                r[5] == null ? null : ((Number) r[5]).doubleValue(),
                r[6] == null ? null : ((Number) r[6]).doubleValue()
        )).toList();

        // labels YYYY-MM ordenadas
        var labels = agg.stream().map(CarMonthAggRowDTO::getMes).distinct().sorted().toList();

        // byCar: carroId -> lista MesValorDTO
        var gastoByCar   = groupToMesValor(agg, CarMonthAggRowDTO::getSomaValor);
        var litrosByCar  = groupToMesValor(agg, CarMonthAggRowDTO::getSomaLitros);
        var eficByCar    = groupToMesValor(agg, CarMonthAggRowDTO::getEficiencia);

        // sum: agregação dos selecionados
        var gastoSum     = sumAcrossCars(labels, gastoByCar);
        var litrosSum    = sumAcrossCars(labels, litrosByCar);
        var eficSum      = efficiencyWeighted(labels, agg); // Σkm / Σlitros

        // cars + kmAtual (via CarroService)
        var optionsRaw = repo.carOptions(carIds);
        var cars = new ArrayList<CarOptionDTO>(optionsRaw.size());
        for (Object[] r : optionsRaw) {
            Integer id = ((Number) r[0]).intValue();
            String placa = Objects.toString(r[1], null);
            Double kmAtual = carroService.getKmRecente(id); // <<< usa serviço
            cars.add(new CarOptionDTO(id, placa, kmAtual));
        }

        SeriesBlockDTO gasto = new SeriesBlockDTO(gastoSum, gastoByCar);
        SeriesBlockDTO litros = new SeriesBlockDTO(litrosSum, litrosByCar);
        SeriesBlockDTO eficiencia = new SeriesBlockDTO(eficSum, eficByCar);

        CarStatsResponseDTO resp = new CarStatsResponseDTO();
        resp.setCars(cars);
        resp.setLabels(labels);
        resp.setGasto(gasto);
        resp.setLitros(litros);
        resp.setEficiencia(eficiencia);
        resp.setMode((mode == null || mode.isBlank()) ? "compare" : mode);
        return resp;
    }

    // ---------- helpers ----------

    private Map<Integer, List<MesValorDTO>> groupToMesValor(
            List<CarMonthAggRowDTO> agg,
            java.util.function.Function<CarMonthAggRowDTO, Double> valueSelector
    ) {
        return agg.stream().collect(Collectors.groupingBy(
                CarMonthAggRowDTO::getCarroId,
                LinkedHashMap::new,
                Collectors.mapping(
                        r -> new MesValorDTO(r.getMes(), valueSelector.apply(r)),
                        Collectors.toList()
                )
        ));
    }

    private List<MesValorDTO> sumAcrossCars(
            List<String> labels,
            Map<Integer, List<MesValorDTO>> byCar
    ) {
        Map<String, Double> sum = new LinkedHashMap<>();
        labels.forEach(l -> sum.put(l, 0.0));
        byCar.values().forEach(list -> list.forEach(p -> {
            if (p.getValor() != null) sum.merge(p.getMes(), p.getValor(), Double::sum);
        }));
        return labels.stream().map(m -> new MesValorDTO(m, sum.get(m))).toList();
    }

    private List<MesValorDTO> efficiencyWeighted(
            List<String> labels,
            List<CarMonthAggRowDTO> agg
    ) {
        Map<String, Double> km = new LinkedHashMap<>();
        Map<String, Double> lt = new LinkedHashMap<>();
        labels.forEach(l -> { km.put(l, 0.0); lt.put(l, 0.0); });

        for (CarMonthAggRowDTO r : agg) {
            if (r.getKmDelta() != null)    km.merge(r.getMes(), r.getKmDelta(), Double::sum);
            if (r.getSomaLitros() != null) lt.merge(r.getMes(), r.getSomaLitros(), Double::sum);
        }

        List<MesValorDTO> out = new ArrayList<>(labels.size());
        for (String m : labels) {
            double L = lt.getOrDefault(m, 0.0);
            if (L == 0.0) {
                out.add(new MesValorDTO(m));
            } else {
                out.add(new MesValorDTO(m, km.getOrDefault(m, 0.0) / L));
            }
        }
        return out;
    }
}