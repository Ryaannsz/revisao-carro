package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.dto.dashboards.MesValorDTO;
import com.revisao.demo.models.Abast;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



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
    List<MesValorDTO> gastoMensal(String empresaId);


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
    List<MesValorDTO> litrosMensais(String empresaId);

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
    List<MesValorDTO> kmRodadoMensal(String empresaId);


@Query(value = """
SELECT
  t.mes,
  CASE
    WHEN t.soma_litros = 0 THEN NULL
    ELSE t.soma_valor / t.soma_litros
  END AS preco_medio
FROM (
  SELECT
    DATE_FORMAT(a.dt_abast, '%Y-%m') AS mes,
    SUM(a.valor_comb)  AS soma_valor,
    SUM(a.litro_comb)  AS soma_litros
  FROM tb_abastecimento a
  JOIN tb_carro c ON c.id = a.carro_id
  WHERE c.empresa_id = :empresaId
  GROUP BY DATE_FORMAT(a.dt_abast, '%Y-%m')
) t
ORDER BY t.mes
""", nativeQuery = true)
List<Object[]> precoMedioLitroMensalRaw(@Param("empresaId") Integer empresaId);





}
