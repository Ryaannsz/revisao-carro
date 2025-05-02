package com.revisao.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revisao.demo.models.Revisao;

public interface RevisaoRepository extends BaseRepository<Revisao, Integer>{

	  @Query(
			    value = """
			      SELECT km_atual
			        FROM TB_REVISAO
			       WHERE carro_id = :idCarro
			       ORDER BY dt_revisao DESC
			       LIMIT 1
			    """,
			    nativeQuery = true
			  )
			  Optional<Double> findLatestKmAtualNative(@Param("idCarro") Integer idCarro);
	
}
