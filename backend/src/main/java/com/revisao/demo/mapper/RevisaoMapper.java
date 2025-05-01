package com.revisao.demo.mapper;

import org.mapstruct.Mapper;

import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.models.Revisao;

@Mapper(componentModel = "spring")
public interface RevisaoMapper extends BaseMapper<RevisaoDTO, Revisao>{

}
