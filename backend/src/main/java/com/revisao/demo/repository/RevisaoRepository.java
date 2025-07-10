package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.models.Revisao;

public interface RevisaoRepository extends BaseRepository<Revisao, Integer> {

    List<Revisao> findByCarro_id(Integer idCarro);

    void deleteByCarro_id(Integer idCarro);

}
