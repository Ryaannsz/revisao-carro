package com.revisao.demo.Mapper;

import java.util.List;

public interface BaseMapper<D, E> {

    E toEntity(D dto);

    D toDTO(E entity);

    List<D> toDTOList(List<E> entityList);

    List<E> toEntityList(List<D> dtoList);
}
