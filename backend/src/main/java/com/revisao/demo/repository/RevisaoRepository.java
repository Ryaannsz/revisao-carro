package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.dto.MesValorDTO;
import com.revisao.demo.models.Revisao;
import org.springframework.data.jpa.repository.Query;

public interface RevisaoRepository extends BaseRepository<Revisao, Integer> {

    List<Revisao> findByCarro_id(Integer idCarro);

    void deleteByCarro_id(Integer idCarro);

    // a partir daqui, dashboard

    @Query("""
    select new com.revisao.demo.dto.MesValorDTO(
      function('DATE_FORMAT', r.dtRevisao, '%Y-%m'), count(r.id)
    )
    from Revisao r
    join r.carro c
    where c.empresa.id = :empresaId
    group by function('DATE_FORMAT', r.dtRevisao, '%Y-%m')
    order by 1
  """)
    List<MesValorDTO> revisoesPorMes(String empresaId);

    @Query("""
    select r from Revisao r
    join r.carro c
    where c.empresa.id = :empresaId
    and r.dtRevisao = (
      select max(r2.dtRevisao) from Revisao r2 where r2.carro.id = c.id
    )
  """)
    List<Revisao> ultimaRevisaoPorCarro(String empresaId);

}
