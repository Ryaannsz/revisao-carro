package com.revisao.demo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.revisao.demo.dto.AbastDTO;
import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.models.Abast;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.User;

public class AbastMapperTest {

    private final AbastMapper mapper = Mappers.getMapper(AbastMapper.class);

    private User user;
    private Carro carro;
    private AbastDTO dto;
    private Abast abast;

    @BeforeEach
    void setUp() {
	user = new User();
	user.setId(1);
	user.setNome("João Silva");
	user.setCpf("123.456.789-00");
	user.setSenha("senha123");
	user.setRoles(UserRoles.USER);

	carro = new Carro();
	carro.setId(10);
	carro.setPlaca("ABC-1234");
	carro.setKmAdicionado(50000.0);
	carro.setDtAdicionado(Date.valueOf("2023-01-01"));
	carro.setMarca(null);
	carro.setModelo(null);

	dto = new AbastDTO();
	dto.setId(200);
	dto.setLitroComb(35.0);
	dto.setValorComb(210.0);
	dto.setDtAbast(Date.valueOf("2024-05-10"));
	dto.setKmAtual(115000.0);
	dto.setUser(user);
	dto.setCarro(carro);

	abast = new Abast(100, 40.5, 250.0, Date.valueOf("2024-05-01"), 120000.0, user, carro);
    }

    @Test
    void testToDTO() {

	AbastDTO dto = mapper.toDTO(abast);

	assertEquals(abast.getId(), dto.getId());
	assertEquals(abast.getLitroComb(), dto.getLitroComb());
	assertEquals(abast.getValorComb(), dto.getValorComb());
	assertEquals(abast.getDtAbast(), dto.getDtAbast());
	assertEquals(abast.getKmAtual(), dto.getKmAtual());
	assertEquals(user.getId(), dto.getUser().getId());
	assertEquals(carro.getId(), dto.getCarro().getId());
    }

    @Test
    void testToEntity() {

	Abast entity = mapper.toEntity(dto);

	assertEquals(dto.getId(), entity.getId());
	assertEquals(dto.getLitroComb(), entity.getLitroComb());
	assertEquals(dto.getValorComb(), entity.getValorComb());
	assertEquals(dto.getDtAbast(), entity.getDtAbast());
	assertEquals(dto.getKmAtual(), entity.getKmAtual());
	assertNotNull(entity.getUser());
	assertEquals(dto.getUser().getId(), entity.getUser().getId());
	assertNotNull(entity.getCarro());
	assertEquals(dto.getCarro().getId(), entity.getCarro().getId());
    }

    @Test
    void testListMapping() {

	List<Abast> entityList = mapper.toEntityList(List.of(dto));
	assertEquals(1, entityList.size());

	List<AbastDTO> dtoList = mapper.toDTOList(entityList);
	assertEquals(1, dtoList.size());
	assertEquals(dto.getId(), dtoList.get(0).getId());
    }
}
