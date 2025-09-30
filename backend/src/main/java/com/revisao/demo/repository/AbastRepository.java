package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.dto.MesValorDTO;
import com.revisao.demo.models.Abast;
import com.revisao.demo.models.Revisao;
import org.springframework.data.jpa.repository.Query;

public interface AbastRepository extends BaseRepository<Abast, Integer> {

    List<Abast> findByCarro_id(Integer carroId);

    void deleteByCarro_id(Integer carroId);

    //dashboard querys aqui:
    @Query("""
  select new com.revisao.demo.dto.MesValorDTO(
    cast(function('DATE_FORMAT', a.dtAbast, '%Y-%m') as string),
    cast(sum(a.valorComb) as double)
  )
  from Abast a
  join a.carro c
  where c.empresa.id = :empresaId
  group by function('DATE_FORMAT', a.dtAbast, '%Y-%m')
  order by function('DATE_FORMAT', a.dtAbast, '%Y-%m')
""")
    List<MesValorDTO> gastoMensal(String empresaId);

    @Query("""
  select new com.revisao.demo.dto.MesValorDTO(
    cast(function('DATE_FORMAT', a.dtAbast, '%Y-%m') as string),
    cast(sum(a.litroComb) as double)
  )
  from Abast a
  join a.carro c
  where c.empresa.id = :empresaId
  group by function('DATE_FORMAT', a.dtAbast, '%Y-%m')
  order by function('DATE_FORMAT', a.dtAbast, '%Y-%m')
""")
    List<MesValorDTO> litrosMensais(String empresaId);

    @Query("""
  select new com.revisao.demo.dto.MesValorDTO(
    cast(function('DATE_FORMAT', a.dtAbast, '%Y-%m') as string),
    cast(max(a.kmAtual) - min(a.kmAtual) as double)
  )
  from Abast a
  where a.carro.empresa.id = :empresaId
  group by function('DATE_FORMAT', a.dtAbast, '%Y-%m')
  order by function('DATE_FORMAT', a.dtAbast, '%Y-%m')
""")
    List<MesValorDTO> kmRodadoMensal(String empresaId);

    @Query(value = """
  with Tot as (
    select
      date_format(a.dt_abast, '%Y-%m') as mes,
      sum(a.valor_comb) as soma_valor,
      sum(a.litro_comb) as soma_litros
    from abast a
    join carro c on c.id = a.carro_id
    where c.empresa_id = :empresaId
    group by date_format(a.dt_abast, '%Y-%m')
  )
  select
    t.mes,
    case when t.soma_litros = 0 then null else t.soma_valor / t.soma_litros end as preco_medio
  from Tot t
  order by t.mes
""", nativeQuery = true)
    List<Object[]> precoMedioLitroMensalRaw(String empresaId);




}
