package com.revisao.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.Revisao;

@Mapper(componentModel = "spring")
public interface CarroMapper extends BaseMapper<CarroDTO, Carro>{

	@Mapping(source = "marca.idMarca",       target = "marca.idMarca")
    @Mapping(source = "modelo.idModelo", target = "modelo.idModelo")
    CarroDTO toDTO(Carro carro);

   
    @Mapping(source = "idModelo", target = "modelo.idModelo")
    @Mapping(source = "idMarca", target = "marca.idMarca")
    Carro toEntity(CarroDTO dto);

}
