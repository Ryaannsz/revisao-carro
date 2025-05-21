package com.revisao.demo.mapper;

import org.mapstruct.Mapper;

import com.revisao.demo.dto.MarcaDTO;
import com.revisao.demo.models.Marca;

@Mapper(componentModel = "spring")
public interface MarcaMapper extends BaseMapper<MarcaDTO, Marca>{

}
