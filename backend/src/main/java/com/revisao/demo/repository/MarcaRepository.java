package com.revisao.demo.repository;

import com.revisao.demo.models.Marca;

import java.util.Optional;

public interface MarcaRepository extends BaseRepository<Marca, Integer>{
	
	boolean existsByMarca(String marca);

    Optional<Marca> findByMarca(String marca);
	
}
