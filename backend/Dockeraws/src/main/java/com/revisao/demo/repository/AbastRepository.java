package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.dto.dashboards.MesValorDTO;
import com.revisao.demo.models.Abast;
import org.springframework.data.jpa.repository.Query;

public interface AbastRepository extends BaseRepository<Abast, Integer> {

    List<Abast> findByCarro_id(Integer carroId);

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

    @Query("""
  select new com.revisao.demo.dto.dashboards.MesValorDTO(
    concat('', function('DATE_FORMAT', a.dtAbast, '%Y-%m')),
    ( coalesce(max(a.kmAtual), 0d) - coalesce(min(a.kmAtual), 0d) ) * 1.0
  )
  from Abast a
  where a.carro.empresa.id = :empresaId
  group by concat('', function('DATE_FORMAT', a.dtAbast, '%Y-%m'))
  order by concat('', function('DATE_FORMAT', a.dtAbast, '%Y-%m'))
""")
    List<MesValorDTO> kmRodadoMensal(Integer empresaId);


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
