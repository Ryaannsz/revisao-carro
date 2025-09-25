package com.revisao.demo.service;

import java.sql.Date;
import java.util.List;

import com.revisao.demo.dto.EmpresaDTO;
import com.revisao.demo.dto.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.mapper.CarroMapper;
import com.revisao.demo.models.Carro;
import com.revisao.demo.repository.AbastRepository;
import com.revisao.demo.repository.CarroRepository;
import com.revisao.demo.repository.RevisaoRepository;

@Service
public class CarroService extends BaseServiceImpl<CarroDTO, Carro, Integer> {

    private final CarroRepository carroRepository;

    private final AbastRepository abastRepository;

    private final RevisaoRepository revisaoRepository;

    public CarroService(CarroRepository carroRepository, CarroMapper carroMapper, AbastRepository abastRepository,
	    RevisaoRepository revisaoRepository) {
	super(carroRepository, carroMapper);
	this.carroRepository = carroRepository;
	this.abastRepository = abastRepository;
	this.revisaoRepository = revisaoRepository;
    }

    public Double getKmRecente(Integer idCarro) {
	return carroRepository.findKmMaisRecenteByIdCarro(idCarro);
    }

    public void salvarCarro(CarroDTO carro) {
	if (carroRepository.existsByPlaca(carro.getPlaca()))
	    throw new IllegalArgumentException("Carro com essa placa já existente!");
	carro.setPlaca(carro.getPlaca().toLowerCase());
	carro.setDtAdicionado(new Date(System.currentTimeMillis()));
	save(carro);
    }

    @Transactional
    public void deleteCarro(Integer carroId) {
	abastRepository.deleteByCarro_id(carroId);
	revisaoRepository.deleteByCarro_id(carroId);
	delete(carroId);
    }

    public List<CarroDTO> getCarrosByEmpresa(Integer empresaId) {
        return mapper.toDTOList(carroRepository.findByEmpresaId(empresaId));
    }

}
