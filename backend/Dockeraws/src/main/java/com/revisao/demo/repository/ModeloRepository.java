package com.revisao.demo.repository;

import com.revisao.demo.models.Marca;
import com.revisao.demo.models.Modelo;

import java.util.List;
import java.util.Optional;

public interface ModeloRepository extends BaseRepository<Modelo, Integer>{
	
	boolean existsByModelo(String modelo);

    Optional<Modelo> findByModelo(String modelo);

    List<Modelo> findAllByEmpresaIdOrderByModeloAsc(Integer empresaId);
}
