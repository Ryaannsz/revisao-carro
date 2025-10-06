package com.revisao.demo.repository;

import com.revisao.demo.models.Abast;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface DashboardRepository extends Repository<Abast, Integer> {

    @Query(value = """
SELECT
  base.carro_id        AS carroId,
  base.placa           AS placa,
  base.mes             AS mes,
  base.soma_valor      AS somaValor,
  base.soma_litros     AS somaLitros,
  (base.max_km - base.min_km) AS kmDelta,
  CASE
    WHEN base.soma_litros = 0 THEN NULL
    ELSE (base.max_km - base.min_km) / base.soma_litros
  END AS eficiencia
FROM (
  SELECT
    a.carro_id,
    c.placa,
    DATE_FORMAT(a.dt_abast, '%Y-%m') AS mes,
    SUM(a.valor_comb)  AS soma_valor,
    SUM(a.litro_comb)  AS soma_litros,
    MAX(a.km_atual)    AS max_km,
    MIN(a.km_atual)    AS min_km
  FROM tb_abastecimento a
  JOIN tb_carro c ON c.id = a.carro_id
  WHERE c.empresa_id = :empresaId
    AND a.carro_id IN (:carIds)
    AND (:startDate IS NULL OR a.dt_abast >= :startDate)
    AND (:endDate   IS NULL OR a.dt_abast <  :endDate)
  GROUP BY a.carro_id, c.placa, DATE_FORMAT(a.dt_abast, '%Y-%m')
) AS base
ORDER BY base.carro_id, base.mes
""", nativeQuery = true)
    List<Object[]> carMonthAgg(
            @Param("carIds")    List<Integer> carIds,
            @Param("startDate") Date startDate,   // inclusivo
            @Param("endDate")   Date endDate,     // exclusivo
            @Param("empresaId") Integer empresaId
    );

    @Query(value = """
SELECT c.id AS carroId, c.placa AS placa
FROM tb_carro c
WHERE c.id IN (:carIds)
ORDER BY c.id
""", nativeQuery = true)
    List<Object[]> carOptions(@Param("carIds") List<Integer> carIds);
}
