package com.revisao.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.revisao.demo.dto.ModeloDTO;
import com.revisao.demo.models.Modelo;
import com.revisao.demo.repository.ModeloRepository;
import com.revisao.demo.mapper.ModeloMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

class ModeloServiceTest {

    @Mock
    private ModeloRepository modeloRepository;

    @Mock
    private ModeloMapper modeloMapper;

    @InjectMocks
    private ModeloService modeloService;

    private Modelo modeloEntity;
    private ModeloDTO modeloDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        modeloEntity = new Modelo(1, "sedan");
        modeloDTO = new ModeloDTO(1, "Sedan");
    }

    @Test
    void salvarModelo_shouldSaveLowerCaseModelo() {
        when(modeloRepository.existsByModelo("sedan")).thenReturn(false);
        when(modeloMapper.toEntity(any(ModeloDTO.class))).thenReturn(modeloEntity);
        when(modeloRepository.saveAndFlush(modeloEntity)).thenReturn(modeloEntity);
        when(modeloMapper.toDTO(modeloEntity)).thenReturn(modeloDTO);

        ModeloDTO result = modeloService.salvarModelo(new ModeloDTO(null, "Sedan"));

        assertNotNull(result);
        assertEquals("Sedan", result.getModelo());
        verify(modeloRepository).existsByModelo("sedan");
        verify(modeloRepository).saveAndFlush(modeloEntity);
    }

    @Test
    void salvarModelo_shouldThrowIfModeloExists() {
        when(modeloRepository.existsByModelo("sedan")).thenReturn(true);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            modeloService.salvarModelo(new ModeloDTO(null, "Sedan"));
        });

        assertEquals("Modelo já adicionado!", thrown.getMessage());
        verify(modeloRepository).existsByModelo("sedan");
        verify(modeloRepository, never()).saveAndFlush(any());
    }

    @Test
    void findAll_shouldReturnList() {
        when(modeloService.findAll()).thenReturn(List.of(modeloDTO));

        List<ModeloDTO> modelos = modeloService.findAll();

        assertFalse(modelos.isEmpty());
        assertEquals("Sedan", modelos.get(0).getModelo());
    }
}

