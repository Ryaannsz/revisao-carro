package com.revisao.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revisao.demo.models.Carro;

import java.util.List;
import java.util.Optional;

public interface CarroRepository extends BaseRepository<Carro, Integer> {

    @Query(value = """
    	    SELECT MAX(km.km_atualizado)
    	    FROM (
    	        SELECT c.id AS id, c.km_adicionado AS km_atualizado
    	        FROM tb_carro c
    	        UNION ALL
    	        SELECT r.carro_id AS id, r.km_atual AS km_atualizado
    	        FROM tb_revisao r
    	        UNION ALL
    	        SELECT a.carro_id AS id, a.km_atual AS km_atualizado
    	        FROM tb_abastecimento a
    	    ) km
    	    WHERE km.id = :carroId
    	""", nativeQuery = true)

    Double findKmMaisRecenteByIdCarro(@Param("carroId") Integer carroId);

    boolean existsByPlaca(String placa);

    List<Carro> findByEmpresaId(@Param("empresaId") Integer empresaId);

    Optional<Carro> findByPlaca(String placa);

}
