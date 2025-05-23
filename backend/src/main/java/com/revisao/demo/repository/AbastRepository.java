package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.models.Abast;

public interface AbastRepository extends BaseRepository<Abast, Integer>{

	List<Abast> findByCarroIdCarro(Integer idCarro);
	
	void deleteByCarro_IdCarro(Integer idCarro);
}
