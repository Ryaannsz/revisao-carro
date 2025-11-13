package com.revisao.demo.dto.dashboards;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResumoDTO {

    private List<MesValorDTO> gastoMensal;
    private List<MesValorDTO> litrosMensais;
    private List<MesValorDTO> kmRodadoMensal;
    private List<MesValorDTO> precoMedioLitroMensal;
    private List<MesValorDTO> eficienciaMensal;
    private List<MesValorDTO> revisoesPorMes;
    private List<ProximaRevisaoDTO> proximasRevisoes;

}
