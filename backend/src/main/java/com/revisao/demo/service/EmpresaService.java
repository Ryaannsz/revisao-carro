package com.revisao.demo.service;

import com.revisao.demo.dto.EmpresaDTO;
import com.revisao.demo.mapper.BaseMapper;
import com.revisao.demo.mapper.EmpresaMapper;
import com.revisao.demo.models.Empresa;
import com.revisao.demo.repository.BaseRepository;
import com.revisao.demo.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService extends BaseServiceImpl<EmpresaDTO, Empresa, Integer>{
    public EmpresaService(EmpresaRepository repository, EmpresaMapper mapper) {
        super(repository, mapper);
    }
}
