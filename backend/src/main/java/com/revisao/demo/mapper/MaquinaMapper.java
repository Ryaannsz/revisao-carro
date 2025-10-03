package com.revisao.demo.mapper;

import org.mapstruct.Mapper;

import com.revisao.demo.dto.MaquinaDTO;
import com.revisao.demo.models.Maquina;

@Mapper(componentModel = "spring")
public interface MaquinaMapper extends BaseMapper<MaquinaDTO, Maquina> {

}
