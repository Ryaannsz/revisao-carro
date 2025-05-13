package com.revisao.demo.repository;

import com.revisao.demo.models.Modelo;

public interface ModeloRepository extends BaseRepository<Modelo, Integer>{
	
	boolean existsByModelo(String modelo);

}
