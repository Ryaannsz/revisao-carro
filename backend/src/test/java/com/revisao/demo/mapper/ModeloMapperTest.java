package com.revisao.demo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.revisao.demo.dto.ModeloDTO;
import com.revisao.demo.models.Modelo;

public class ModeloMapperTest {

    private final ModeloMapper mapper = Mappers.getMapper(ModeloMapper.class);


    @Test
    void testToEntity() {
        ModeloDTO dto = new ModeloDTO(1, "Civic");
        Modelo entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getIdModelo(), entity.getIdModelo());
        assertEquals(dto.getModelo(), entity.getModelo());
    }

    @Test
    void testToDTO() {
        Modelo entity = new Modelo();
        entity.setIdModelo(2);
        entity.setModelo("Corolla");

        ModeloDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getIdModelo(), dto.getIdModelo());
        assertEquals(entity.getModelo(), dto.getModelo());
    }

    @Test
    void testToDTOList() {
        Modelo m1 = new Modelo();
        m1.setIdModelo(1);
        m1.setModelo("Fiesta");

        Modelo m2 = new Modelo();
        m2.setIdModelo(2);
        m2.setModelo("Focus");

        List<Modelo> entities = Arrays.asList(m1, m2);

        List<ModeloDTO> dtos = mapper.toDTOList(entities);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Fiesta", dtos.get(0).getModelo());
        assertEquals("Focus", dtos.get(1).getModelo());
    }

    @Test
    void testToEntityList() {
        ModeloDTO dto1 = new ModeloDTO(3, "Golf");
        ModeloDTO dto2 = new ModeloDTO(4, "Polo");

        List<ModeloDTO> dtos = Arrays.asList(dto1, dto2);

        List<Modelo> entities = mapper.toEntityList(dtos);

        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals("Golf", entities.get(0).getModelo());
        assertEquals("Polo", entities.get(1).getModelo());
    }
}
