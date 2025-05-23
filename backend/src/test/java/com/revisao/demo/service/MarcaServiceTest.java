package com.revisao.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.revisao.demo.dto.MarcaDTO;
import com.revisao.demo.mapper.MarcaMapper;
import com.revisao.demo.models.Marca;
import com.revisao.demo.repository.MarcaRepository;


class MarcaServiceTest {

    @Mock
    private MarcaRepository marcaRepository;

    @Mock
    private MarcaMapper marcaMapper;

    @InjectMocks
    private MarcaService marcaService;

    private Marca marcaEntity;
    private MarcaDTO marcaDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        marcaEntity = new Marca(1, "fiat");
        marcaDTO = new MarcaDTO(1, "Fiat");
    }

    @Test
    void salvarMarca_shouldSaveLowerCaseMarca() {
        // Marca já em lowercase
        when(marcaRepository.existsByMarca("fiat")).thenReturn(false);
        when(marcaMapper.toEntity(any(MarcaDTO.class))).thenReturn(marcaEntity);
        when(marcaRepository.saveAndFlush(marcaEntity)).thenReturn(marcaEntity);
        when(marcaMapper.toDTO(marcaEntity)).thenReturn(marcaDTO);

        MarcaDTO result = marcaService.salvarMarca(new MarcaDTO(null, "Fiat"));

        assertNotNull(result);
        assertEquals("Fiat", result.getMarca());  // Retorna como está no DTO mockado
        verify(marcaRepository).existsByMarca("fiat");
        verify(marcaRepository).saveAndFlush(marcaEntity);
    }

    @Test
    void salvarMarca_shouldThrowIfMarcaExists() {
        when(marcaRepository.existsByMarca("fiat")).thenReturn(true);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            marcaService.salvarMarca(new MarcaDTO(null, "Fiat"));
        });

        assertEquals("Marca já adicionada!", thrown.getMessage());
        verify(marcaRepository).existsByMarca("fiat");
        verify(marcaRepository, never()).saveAndFlush(any());
    }

    @Test
    void findAll_shouldReturnList() {
        when(marcaService.findAll()).thenReturn(List.of(marcaDTO));

        List<MarcaDTO> marcas = marcaService.findAll();

        assertFalse(marcas.isEmpty());
        assertEquals("Fiat", marcas.get(0).getMarca());
    }
}
