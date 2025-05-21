package com.revisao.demo.mapper;

import org.mapstruct.Mapper;

import com.revisao.demo.dto.ModeloDTO;
import com.revisao.demo.models.Modelo;

@Mapper(componentModel = "spring")
public interface ModeloMapper extends BaseMapper<ModeloDTO, Modelo>{
	
}
