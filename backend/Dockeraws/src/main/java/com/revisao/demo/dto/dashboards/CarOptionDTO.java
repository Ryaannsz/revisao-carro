package com.revisao.demo.dto.dashboards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarOptionDTO {
    private Integer id;
    private String label;
    private Double kmAtual;
}
