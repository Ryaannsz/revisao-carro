package com.revisao.demo.dto.dashboards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarMonthAggRowDTO {
    private Integer carroId;
    private String  placa;
    private String  mes;
    private Double  somaValor;
    private Double  somaLitros;
    private Double  kmDelta;
    private Double  eficiencia;
}
