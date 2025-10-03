package com.revisao.demo.repository;

import com.revisao.demo.models.Abast;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface DashboardRepository extends Repository<Abast, Integer> {

    @Query(value = """
with base as (
  select
    a.carro_id,
    c.placa,
    date_format(a.dt_abast, '%Y-%m') as mes,
    sum(a.valor_comb)   as soma_valor,
    sum(a.litro_comb)   as soma_litros,
    max(a.km_atual)     as max_km,
    min(a.km_atual)     as min_km
  from tb_abastecimento a
  join tb_carro c on c.id = a.carro_id
  where c.empresa_id = :empresaId                          -- << filtro por empresa
    and a.carro_id in (:carIds)
    and (:startDate is null or a.dt_abast >= :startDate)
    and (:endDate   is null or a.dt_abast <  :endDate)
  group by a.carro_id, c.placa, date_format(a.dt_abast, '%Y-%m')
)
select
  carro_id         as carroId,
  placa            as placa,
  mes              as mes,
  soma_valor       as somaValor,
  soma_litros      as somaLitros,
  (max_km - min_km)                      as kmDelta,
  case when soma_litros = 0 then null
       else (max_km - min_km) / soma_litros end as eficiencia
from base
order by carroId, mes
""", nativeQuery = true)
    List<Object[]> carMonthAgg(
            @Param("carIds")    List<Integer> carIds,
            @Param("startDate") Date startDate,   // inclusive
            @Param("endDate")   Date endDate,     // exclusivo
            @Param("empresaId") Integer empresaId
    );

    @Query(value = """
    select c.id as carroId, c.placa as placa
    from tb_carro c
    where c.id in (:carIds)
    order by c.id
    """, nativeQuery = true)
    List<Object[]> carOptions(@Param("carIds") List<Integer> carIds);
}