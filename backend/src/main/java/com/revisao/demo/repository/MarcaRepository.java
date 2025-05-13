package com.revisao.demo.repository;

import com.revisao.demo.models.Marca;

public interface MarcaRepository extends BaseRepository<Marca, Integer>{
	
	boolean existsByMarca(String marca);
	
}
