package com.revisao.demo.service;

import java.util.List;

import java.util.Optional;

import com.revisao.demo.mapper.BaseMapper;
import com.revisao.demo.repository.BaseRepository;

public abstract class BaseServiceImpl<D, E, ID> implements BaseService<D, ID> {

    protected final BaseRepository<E, ID> repository;
    protected final BaseMapper<D, E> mapper;

    public BaseServiceImpl(BaseRepository<E, ID> repository, BaseMapper<D, E> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public D save(D dto) {
        E entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public D update(ID id, D dto) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Registro não encontrado para update.");
        }
        E entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<D> findById(ID id) {
        return repository.findById(id)
                         .map(mapper::toDTO);
    }

    @Override
    public List<D> findAll() {
        return mapper.toDTOList(repository.findAll());
    }
}
