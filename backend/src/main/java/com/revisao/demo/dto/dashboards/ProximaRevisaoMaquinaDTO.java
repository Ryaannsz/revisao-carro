package com.revisao.demo.dto.dashboards;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProximaRevisaoMaquinaDTO {
    private Integer maquinaId;
    private String identificacao;
    /** Data da última manutenção (null = nunca teve, base é o cadastro). */
    private Date ultimaRevisao;
    /** Horímetro em que a próxima preventiva vence. */
    private Double horasRevisaoEstimada;
    private Double horasAtual;
    /** Data em que a próxima preventiva vence pelo critério de meses. */
    private Date proximaRevisaoEstimada;
    /** Intervalo usado no cálculo (para a barra de progresso no front). */
    private Double intervaloHoras;
}
