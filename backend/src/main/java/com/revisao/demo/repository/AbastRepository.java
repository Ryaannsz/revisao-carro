package com.revisao.demo.repository;

import java.util.List;
import java.util.Optional;

import com.revisao.demo.dto.dashboards.MesValorDTO;
import com.revisao.demo.models.Abast;
import org.springframework.data.jpa.repository.Query;

public interface AbastRepository extends BaseRepository<Abast, Integer> {

    List<Abast> findByCarro_id(Integer carroId);

    List<Abast> findByCarro_IdOrderByDtAbastDescIdDesc(Integer carroId);

    List<Abast> findByCarro_Empresa_IdOrderByDtAbastDescIdDesc(Integer empresaId);

    Optional<Abast> findByIdAndCarro_Empresa_Id(Integer id, Integer empresaId);

    boolean existsByCarro_Empresa_Id(Integer empresaId);

    void deleteByCarro_id(Integer carroId);

    //dashboard querys aqui:
    @Query("""
  select new com.revisao.demo.dto.dashboards.MesValorDTO(
    concat('', function('DATE_FORMAT', a.dtAbast, '%Y-%m')),
    sum(a.valorComb)
  )
  from Abast a
  join a.carro c
  where c.empresa.id = :empresaId
  group by concat('', function('DATE_FORMAT', a.dtAbast, '%Y-%m'))
  order by concat('', function('DATE_FORMAT', a.dtAbast, '%Y-%m'))
""")
    List<MesValorDTO> gastoMensal(Integer empresaId);


    @Query("""
  select new com.revisao.demo.dto.dashboards.MesValorDTO(
    concat('', function('DATE_FORMAT', a.dtAbast, '%Y-%m')),
    sum(a.litroComb)
  )
  from Abast a
  join a.carro c
  where c.empresa.id = :empresaId
  group by concat('', function('DATE_FORMAT', a.dtAbast, '%Y-%m'))
  order by concat('', function('DATE_FORMAT', a.dtAbast, '%Y-%m'))
""")
    List<MesValorDTO> litrosMensais(Integer empresaId);

    /**
     * Km rodado no mês = soma, carro a carro, de (maior km - menor km) no mês.
     * Antes o max/min era da frota inteira, misturando odômetros de carros diferentes
     * (e o km/L do painel saía em 60-300 km/L).
     */
    @Query(value = """
  select t.mes, sum(t.delta) from (
    select date_format(a.dt_abast, '%Y-%m') as mes, a.carro_id,
           max(a.km_atual) - min(a.km_atual) as delta
    from tb_abastecimento a
    join tb_carro c on c.id = a.carro_id
    where c.empresa_id = :empresaId
    group by date_format(a.dt_abast, '%Y-%m'), a.carro_id
  ) t
  group by t.mes
  order by t.mes
""", nativeQuery = true)
    List<Object[]> kmRodadoMensalRaw(Integer empresaId);


    @Query(value = """
  with Tot as (
    select
      date_format(a.dt_abast, '%Y-%m') as mes,
      sum(a.valor_comb) as soma_valor,
      sum(a.litro_comb) as soma_litros
    from tb_abastecimento a
    join tb_carro c on c.id = a.carro_id
    where c.empresa_id = :empresaId
    group by date_format(a.dt_abast, '%Y-%m')
  )
  select
    t.mes,
    case when t.soma_litros = 0 then null else t.soma_valor / t.soma_litros end as preco_medio
  from Tot t
  order by t.mes
""", nativeQuery = true)
    List<Object[]> precoMedioLitroMensalRaw(Integer empresaId);




}
