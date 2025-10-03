package com.revisao.demo.dto.dashboards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeriesBlockDTO {
    private List<MesValorDTO> sum;
    private Map<Integer, List<MesValorDTO>> byCar;
}
