package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.models.LeituraHorimetro;

public interface LeituraHorimetroRepository extends BaseRepository<LeituraHorimetro, Integer> {

    List<LeituraHorimetro> findByMaquina_IdOrderByDtLeituraDescIdDesc(Integer maquinaId);

    void deleteByMaquina_Id(Integer maquinaId);
}
