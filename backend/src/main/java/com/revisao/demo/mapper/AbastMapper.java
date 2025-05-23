package com.revisao.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.revisao.demo.dto.AbastDTO;
import com.revisao.demo.models.Abast;

@Mapper(componentModel = "spring")
public interface AbastMapper extends BaseMapper<AbastDTO, Abast>{


}
