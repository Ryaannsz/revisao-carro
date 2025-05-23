package com.revisao.demo.service;


import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revisao.demo.dto.AbastDTO;
import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.mapper.AbastMapper;
import com.revisao.demo.models.Abast;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.AbastRepository;

@ExtendWith(MockitoExtension.class)
class AbastServiceTest {

    @Mock
    private AbastRepository abastRepository;

    @Mock
    private AbastMapper abastMapper;

    @Mock
    private CarroService carroService;

    @InjectMocks
    private AbastService abastService;

    private Abast abastEntity;
    private AbastDTO abastDTO;

    @BeforeEach
    void setup() {
        User user = new User();
        Carro carro = new Carro();
        carro.setIdCarro(1);

        abastEntity = new Abast(1, 50.0, 200.0, new Timestamp(System.currentTimeMillis()), 10000.0, user, carro);
        abastDTO = new AbastDTO(1, 50.0, 200.0, 10000.0, null, user, carro);
    }

    @Test
    void saveAbast_shouldSetTimestampIfNullAndSave() {
        abastDTO.setDtAbast(null);

        when(carroService.getKmRecente(1)).thenReturn(9000.0);
        when(abastMapper.toEntity(any())).thenReturn(abastEntity);
        when(abastRepository.save(any())).thenReturn(abastEntity);
        when(abastMapper.toDTO(any())).thenReturn(abastDTO);

        AbastDTO saved = abastService.saveAbast(abastDTO);

        assertNotNull(saved.getDtAbast());
        verify(abastRepository).save(any());
    }

    @Test
    void saveAbast_shouldThrowIfKmAtualIsLowerOrEqualThanKmRecente() {
        abastDTO.setKmAtual(8000.0);

        when(carroService.getKmRecente(1)).thenReturn(8500.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> abastService.saveAbast(abastDTO));

        assertEquals("Quilometragem inferior a atual.", ex.getMessage());
        verify(abastRepository, never()).save(any());
    }

    @Test
    void findAllByCarroId_shouldReturnMappedDTOs() {
        List<Abast> entities = List.of(abastEntity);
        List<AbastDTO> dtos = List.of(abastDTO);

        when(abastRepository.findByCarroIdCarro(1)).thenReturn(entities);
        when(abastMapper.toDTOList(entities)).thenReturn(dtos);

        List<AbastDTO> result = abastService.findAllByCarroId(1);

        assertEquals(1, result.size());
        assertEquals(abastDTO, result.get(0));
    }
    
    @Test
    void saveAbast_shouldPropagateExceptionFromRepository() {
        abastDTO.setKmAtual(12000.0);
        abastDTO.setDtAbast(null);

        when(carroService.getKmRecente(1)).thenReturn(10000.0);
        when(abastMapper.toEntity(any())).thenReturn(abastEntity);
        when(abastRepository.save(any())).thenThrow(new RuntimeException("Erro ao salvar"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> abastService.saveAbast(abastDTO));

        assertEquals("Erro ao salvar", ex.getMessage());
        verify(abastRepository).save(any());
    }
    
    @Test
    void saveAbast_shouldCallMapperCorrectly() {
        abastDTO.setKmAtual(12000.0);
        abastDTO.setDtAbast(null);

        when(carroService.getKmRecente(1)).thenReturn(10000.0);
        when(abastMapper.toEntity(abastDTO)).thenReturn(abastEntity);
        when(abastRepository.save(abastEntity)).thenReturn(abastEntity);
        when(abastMapper.toDTO(abastEntity)).thenReturn(abastDTO);

        AbastDTO result = abastService.saveAbast(abastDTO);

        verify(abastMapper).toEntity(abastDTO);
        verify(abastRepository).save(abastEntity);
        verify(abastMapper).toDTO(abastEntity);

        assertEquals(abastDTO, result);
    }



}
