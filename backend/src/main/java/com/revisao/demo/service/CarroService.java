package com.revisao.demo.service;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.mapper.CarroMapper;
import com.revisao.demo.models.Carro;
import com.revisao.demo.repository.CarroRepository;

@Service
public class CarroService extends BaseServiceImpl<CarroDTO, Carro, Integer>{

	private final CarroRepository carroRepository;
	
	public CarroService(CarroRepository carroRepository, CarroMapper carroMapper) {
		super(carroRepository, carroMapper);
		this.carroRepository=carroRepository;
	}
	
	public Double getKmRecente(Integer idCarro) {
		return carroRepository
	            .findLatestKmAtualNative(idCarro)
	            .orElse(null);
	}


}
