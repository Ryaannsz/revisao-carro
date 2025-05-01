package com.revisao.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.models.Revisao;

@Mapper(componentModel = "spring")
public interface RevisaoMapper extends BaseMapper<RevisaoDTO, Revisao>{

	@Mapping(source="user.idUser", target = "idUser")
	@Mapping(source = "carro.idCarro", target = "idCarro")
	RevisaoDTO toDTO(Revisao revisao);
	
	@Mapping(source = "idUser", target = "user.idUser")
	@Mapping(source = "idCarro", target = "carro.idCarro")
	Revisao toEntity(RevisaoDTO revisao);
}
