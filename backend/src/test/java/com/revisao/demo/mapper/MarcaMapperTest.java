package com.revisao.demo.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.revisao.demo.dto.MarcaDTO;
import com.revisao.demo.models.Marca;

public class MarcaMapperTest {

    private final MarcaMapper mapper = Mappers.getMapper(MarcaMapper.class);


    @Test
    void testToEntity() {
        MarcaDTO dto = new MarcaDTO(1, "Toyota");
        Marca entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getIdMarca(), entity.getIdMarca());
        assertEquals(dto.getMarca(), entity.getMarca());
    }

    @Test
    void testToDTO() {
        Marca entity = new Marca();
        entity.setIdMarca(2);
        entity.setMarca("Honda");

        MarcaDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getIdMarca(), dto.getIdMarca());
        assertEquals(entity.getMarca(), dto.getMarca());
    }

    @Test
    void testToDTOList() {
        Marca m1 = new Marca();
        m1.setIdMarca(1);
        m1.setMarca("Ford");

        Marca m2 = new Marca();
        m2.setIdMarca(2);
        m2.setMarca("Chevrolet");

        List<Marca> entities = Arrays.asList(m1, m2);

        List<MarcaDTO> dtos = mapper.toDTOList(entities);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Ford", dtos.get(0).getMarca());
        assertEquals("Chevrolet", dtos.get(1).getMarca());
    }

    @Test
    void testToEntityList() {
        MarcaDTO dto1 = new MarcaDTO(3, "BMW");
        MarcaDTO dto2 = new MarcaDTO(4, "Audi");

        List<MarcaDTO> dtos = Arrays.asList(dto1, dto2);

        List<Marca> entities = mapper.toEntityList(dtos);

        assertNotNull(entities);
        assertEquals(2, entities.size());
        assertEquals("BMW", entities.get(0).getMarca());
        assertEquals("Audi", entities.get(1).getMarca());
    }
}