package com.revisao.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.revisao.demo.models.Maquina;

public interface MaquinaRepository extends BaseRepository<Maquina, Integer> {

    List<Maquina> findByEmpresaIdOrderByIdAsc(Integer empresaId);

    Optional<Maquina> findByIdAndEmpresaId(Integer id, Integer empresaId);

    boolean existsByChassiIgnoreCaseAndEmpresaId(String chassi, Integer empresaId);

    /** Horímetro mais recente: maior valor entre cadastro, manutenções e leituras avulsas. */
    @Query(value = """
        SELECT MAX(h.horas) FROM (
            SELECT m.id AS id, m.horas AS horas FROM tb_maquina m
            UNION ALL
            SELECT r.maquina_id AS id, r.horas_atual AS horas FROM tb_revisao_maquina r
            UNION ALL
            SELECT l.maquina_id AS id, l.horas AS horas FROM tb_leitura_horimetro l
        ) h
        WHERE h.id = :maquinaId
        """, nativeQuery = true)
    Double findHorasMaisRecente(@Param("maquinaId") Integer maquinaId);
}
