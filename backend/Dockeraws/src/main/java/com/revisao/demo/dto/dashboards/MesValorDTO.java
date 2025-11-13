package com.revisao.demo.dto.dashboards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class MesValorDTO {

    private String mes;
    private Double valor;

    public MesValorDTO(String mes, Long valor) {
        this.mes = mes;
        this.valor = valor.doubleValue();
    }

    public MesValorDTO(String mes) {
        this.mes = mes;
    }


}
