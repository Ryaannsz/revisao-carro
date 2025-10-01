package com.revisao.demo.repository;

import com.revisao.demo.models.Modelo;

import java.util.Optional;

public interface ModeloRepository extends BaseRepository<Modelo, Integer>{
	
	boolean existsByModelo(String modelo);

    Optional<Modelo> findByModelo(String modelo);

}
