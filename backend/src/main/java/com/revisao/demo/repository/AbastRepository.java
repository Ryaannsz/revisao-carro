package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.models.Abast;

public interface AbastRepository extends BaseRepository<Abast, Integer> {

    List<Abast> findByCarro_id(Integer carroId);

    void deleteByCarro_id(Integer carroId);
}
