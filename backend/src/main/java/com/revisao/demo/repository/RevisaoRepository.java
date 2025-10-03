package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.dto.dashboards.MesValorDTO;
import com.revisao.demo.models.Revisao;
import org.springframework.data.jpa.repository.Query;

public interface RevisaoRepository extends BaseRepository<Revisao, Integer> {

    List<Revisao> findByCarro_id(Integer idCarro);

    void deleteByCarro_id(Integer idCarro);

    // a partir daqui, dashboard

    @Query("""
      select new com.revisao.demo.dto.dashboards.MesValorDTO(
        concat('', function('DATE_FORMAT', r.dtRevisao, '%Y-%m')),
        count(r.id)
      )
      from Revisao r
      join r.carro c
      where c.empresa.id = :empresaId
      group by concat('', function('DATE_FORMAT', r.dtRevisao, '%Y-%m'))
      order by concat('', function('DATE_FORMAT', r.dtRevisao, '%Y-%m'))
    """)
    List<MesValorDTO> revisoesPorMes(String empresaId);

    @Query("""
  select r from Revisao r
  join r.carro c
  where c.empresa.id = :empresaId
    and r.dtRevisao = (
      select max(r2.dtRevisao) from Revisao r2 where r2.carro = r.carro
    )
    and r.id = (
      select max(r3.id) from Revisao r3
      where r3.carro = r.carro and r3.dtRevisao = r.dtRevisao
    )
""")
    List<Revisao> ultimaRevisaoPorCarro(String empresaId);

}
