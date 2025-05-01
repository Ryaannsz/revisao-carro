package com.revisao.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.mapper.CarroMapper;
import com.revisao.demo.models.Carro;
import com.revisao.demo.repository.CarroRepository;

@Service
public class CarroService extends BaseServiceImpl<CarroDTO, Carro, Integer>{

	public CarroService(CarroRepository carroRepository, CarroMapper carroMapper) {
		super(carroRepository, carroMapper);
	}

	public List<CarroDTO> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
}
