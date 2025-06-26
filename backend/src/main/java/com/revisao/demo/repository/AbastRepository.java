package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.models.Abast;

public interface AbastRepository extends BaseRepository<Abast, Integer> {

    List<Abast> findByCarro_idCarro(Integer idCarro);

    void deleteByCarro_idCarro(Integer idCarro);
}
