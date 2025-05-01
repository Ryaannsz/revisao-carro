package com.revisao.demo.mapper;

import org.mapstruct.Mapper;

import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.models.Carro;

@Mapper(componentModel = "spring")
public interface CarroMapper extends BaseMapper<CarroDTO, Carro>{

}
