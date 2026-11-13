package com.revisao.demo.repository;

import java.util.List;
import java.util.Optional;

import com.revisao.demo.models.RevisaoMaquina;

public interface RevisaoMaquinaRepository extends BaseRepository<RevisaoMaquina, Integer> {

    List<RevisaoMaquina> findByMaquina_Empresa_IdOrderByDtRevisaoDescIdDesc(Integer empresaId);

    List<RevisaoMaquina> findByMaquina_IdOrderByDtRevisaoDescIdDesc(Integer maquinaId);

    Optional<RevisaoMaquina> findByIdAndMaquina_Empresa_Id(Integer id, Integer empresaId);

    void deleteByMaquina_Id(Integer maquinaId);

    Optional<RevisaoMaquina> findFirstByMaquina_IdOrderByDtRevisaoDescIdDesc(Integer maquinaId);
}
