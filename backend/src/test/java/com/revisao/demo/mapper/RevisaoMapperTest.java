package com.revisao.demo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.Revisao;
import com.revisao.demo.models.User;

public class RevisaoMapperTest {

    private final RevisaoMapper mapper = Mappers.getMapper(RevisaoMapper.class);

    private User user;
    private Carro carro;
    private RevisaoDTO dto;

    @BeforeEach
    void setUp() {
	user = new User();
	user.setId(1);
	user.setNome("Maria Souza");
	user.setCpf("987.654.321-00");
	user.setSenha("senha456");
	user.setRoles(UserRoles.USER);

	carro = new Carro();
	carro.setId(20);
	carro.setPlaca("XYZ-5678");
	carro.setKmAdicionado(30000.0);
	carro.setDtAdicionado(Date.valueOf("2022-01-01"));
	carro.setMarca(null);
	carro.setModelo(null);

	dto = new RevisaoDTO();
	dto.setId(300);
	dto.setDtRevisao(Date.valueOf("2024-06-15"));
	dto.setKmAtual(150000.0);
	dto.setUser(user);
	dto.setCarro(carro);
    }

    @Test
    void testToDTO() {
	Revisao revisao = new Revisao(101, Date.valueOf("2024-05-01"), 140000.0, user, carro);

	RevisaoDTO dto = mapper.toDTO(revisao);

	assertEquals(revisao.getId(), dto.getId());
	assertEquals(revisao.getDtRevisao(), dto.getDtRevisao());
	assertEquals(revisao.getKmAtual(), dto.getKmAtual());
	assertEquals(user.getId(), dto.getUser().getId());
	assertEquals(carro.getId(), dto.getCarro().getId());
    }

    @Test
    void testToEntity() {
	Revisao entity = mapper.toEntity(dto);

	assertEquals(dto.getId(), entity.getId());
	assertEquals(dto.getDtRevisao(), entity.getDtRevisao());
	assertEquals(dto.getKmAtual(), entity.getKmAtual());

	assertNotNull(entity.getUser());
	assertEquals(dto.getUser().getId(), entity.getUser().getId());

	assertNotNull(entity.getCarro());
	assertEquals(dto.getCarro().getId(), entity.getCarro().getId());
    }

    @Test
    void testListMapping() {
	List<Revisao> entityList = mapper.toEntityList(List.of(dto));
	assertEquals(1, entityList.size());

	List<RevisaoDTO> dtoList = mapper.toDTOList(entityList);
	assertEquals(1, dtoList.size());
	assertEquals(dto.getId(), dtoList.get(0).getId());
    }
}
