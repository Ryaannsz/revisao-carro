package com.revisao.demo.mapper;

import org.mapstruct.Mapper;

import com.revisao.demo.dto.RevisaoMaquinaDTO;
import com.revisao.demo.models.RevisaoMaquina;

@Mapper(componentModel = "spring")
public interface RevisaoMaquinaMapper extends BaseMapper<RevisaoMaquinaDTO, RevisaoMaquina> {

}
