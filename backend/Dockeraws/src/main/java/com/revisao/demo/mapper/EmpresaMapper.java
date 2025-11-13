package com.revisao.demo.mapper;

import com.revisao.demo.dto.EmpresaDTO;
import com.revisao.demo.models.Empresa;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmpresaMapper extends BaseMapper<EmpresaDTO, Empresa> {
}
