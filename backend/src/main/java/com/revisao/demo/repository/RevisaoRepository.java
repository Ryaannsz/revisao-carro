package com.revisao.demo.repository;

import java.util.List;

import com.revisao.demo.models.Revisao;

public interface RevisaoRepository extends BaseRepository<Revisao, Integer>{

	List<Revisao> findByCarroIdCarro(Integer idCarro);
	
	void deleteByCarro_IdCarro(Integer idCarro);
	
}
