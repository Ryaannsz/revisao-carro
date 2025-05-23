package com.revisao.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.Revisao;

@Mapper(componentModel = "spring")
public interface CarroMapper extends BaseMapper<CarroDTO, Carro>{


}
