package com.revisao.demo.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.revisao.demo.dto.EmpresaDTO;
import com.revisao.demo.dto.UserDTO;
import com.revisao.demo.dto.dashboards.CarOptionDTO;
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

    private final EmpresaService empresaService;

    public CarroService(CarroRepository carroRepository, CarroMapper carroMapper, AbastRepository abastRepository,
	    RevisaoRepository revisaoRepository,
        EmpresaService empresaService) {
	super(carroRepository, carroMapper);
	this.carroRepository = carroRepository;
	this.abastRepository = abastRepository;
	this.revisaoRepository = revisaoRepository;
    this.empresaService = empresaService;
    }

    public Double getKmRecente(Integer idCarro) {
	return carroRepository.findKmMaisRecenteByIdCarro(idCarro);
    }

    public void salvarCarro(CarroDTO carro, Integer empresaId) {
	if (carroRepository.existsByPlaca(carro.getPlaca()))
	    throw new IllegalArgumentException("Carro com essa placa já existente!");
	carro.setPlaca(carro.getPlaca().toLowerCase());
    carro.setEmpresa(empresaService.findById(empresaId).orElseThrow( () -> new IllegalStateException("Não deveria conseguir cadastrar por não ter empresa")));
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

    public List<CarOptionDTO> convertToCarOption(List<CarroDTO> carros){
        List<CarOptionDTO> carOptionDTOs = new ArrayList<>();
        carros.forEach(carro -> {
            CarOptionDTO carOptionDTO = new CarOptionDTO();
            carOptionDTO.setId(carro.getId());
            carOptionDTO.setLabel(carro.getPlaca());
            carOptionDTO.setKmAtual(getKmRecente(carro.getId()));
            carOptionDTOs.add(carOptionDTO);
        });
        return carOptionDTOs;
    }

    public List<CarOptionDTO> getCarOptions(Integer empresaId) {
        return convertToCarOption(getCarrosByEmpresa(empresaId));
    }


}
