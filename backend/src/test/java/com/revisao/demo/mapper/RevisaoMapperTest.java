package com.revisao.demo.mapper;

import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.mapper.RevisaoMapper;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.Revisao;
import com.revisao.demo.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RevisaoMapperTest {

    private final RevisaoMapper mapper = Mappers.getMapper(RevisaoMapper.class);

    private User user;
    private Carro carro;
    private RevisaoDTO dto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setIdUser(1);
        user.setNome("Maria Souza");
        user.setCpf("987.654.321-00");
        user.setSenha("senha456");
        user.setRoles(UserRoles.USER);

        carro = new Carro();
        carro.setIdCarro(20);
        carro.setPlaca("XYZ-5678");
        carro.setKmAdicionado(30000.0);
        carro.setDtAdicionado(Timestamp.valueOf("2022-01-01 00:00:00"));
        carro.setMarca(null);
        carro.setModelo(null);

        dto = new RevisaoDTO();
        dto.setIdRevisao(300);
        dto.setDtRevisao(Timestamp.valueOf("2024-06-15 08:30:00"));
        dto.setKmAtual(150000.0);
        dto.setUser(user);
        dto.setCarro(carro);
    }

    @Test
    void testToDTO() {
        Revisao revisao = new Revisao(
            101,
            Timestamp.valueOf("2024-05-01 14:00:00"),
            140000.0,
            user,
            carro
        );

        RevisaoDTO dto = mapper.toDTO(revisao);

        assertEquals(revisao.getIdRevisao(), dto.getIdRevisao());
        assertEquals(revisao.getDtRevisao(), dto.getDtRevisao());
        assertEquals(revisao.getKmAtual(), dto.getKmAtual());
        assertEquals(user.getIdUser(), dto.getUser().getIdUser());
        assertEquals(carro.getIdCarro(), dto.getCarro().getIdCarro());
    }

    @Test
    void testToEntity() {
        Revisao entity = mapper.toEntity(dto);

        assertEquals(dto.getIdRevisao(), entity.getIdRevisao());
        assertEquals(dto.getDtRevisao(), entity.getDtRevisao());
        assertEquals(dto.getKmAtual(), entity.getKmAtual());

        assertNotNull(entity.getUser());
        assertEquals(dto.getUser().getIdUser(), entity.getUser().getIdUser());

        assertNotNull(entity.getCarro());
        assertEquals(dto.getCarro().getIdCarro(), entity.getCarro().getIdCarro());
    }

    @Test
    void testListMapping() {
        List<Revisao> entityList = mapper.toEntityList(List.of(dto));
        assertEquals(1, entityList.size());

        List<RevisaoDTO> dtoList = mapper.toDTOList(entityList);
        assertEquals(1, dtoList.size());
        assertEquals(dto.getIdRevisao(), dtoList.get(0).getIdRevisao());
    }
}

