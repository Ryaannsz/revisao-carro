package com.revisao.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revisao.demo.models.Carro;

public interface CarroRepository extends BaseRepository<Carro, Integer>{

	@Query(value = """
	        SELECT km.km_atualizado
	        FROM (
	            SELECT c.id_carro, c.km_adicionado AS km_atualizado, c.dt_adicionado AS data_registro
	            FROM tb_carro c
	            UNION ALL
	            SELECT r.id_carro, r.km_atual AS km_atualizado, r.dt_revisao AS data_registro
	            FROM tb_revisao r
	            UNION ALL
	            SELECT a.id_carro, a.km_atual AS km_atualizado, a.dt_abast AS data_registro
	            FROM tb_abastecimento a
	        ) km
	        WHERE km.id_carro = :idCarro
	        ORDER BY km.data_registro DESC
	        LIMIT 1
	        """, nativeQuery = true)
	    Double findKmMaisRecenteByIdCarro(@Param("idCarro") Integer idCarro);
	
		boolean existsByPlaca(String placa);
	
}
