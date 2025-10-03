package com.revisao.demo.dto.dashboards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarStatsResponseDTO {

    private List<CarOptionDTO> cars;
    private List<String> labels;
    private SeriesBlockDTO gasto;
    private SeriesBlockDTO litros;
    private SeriesBlockDTO eficiencia;
    private String mode;

}
