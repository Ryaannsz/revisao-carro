package com.revisao.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.mapper.CarroMapper;
import com.revisao.demo.models.Carro;
import com.revisao.demo.repository.AbastRepository;
import com.revisao.demo.repository.CarroRepository;
import com.revisao.demo.repository.RevisaoRepository;

class CarroServiceTest {

    @Mock
    private CarroRepository carroRepository;

    @Mock
    private CarroMapper carroMapper;
    
    @Mock
    private AbastRepository abastRepository;

    @Mock
    private RevisaoRepository revisaoRepository;

    @InjectMocks
    private CarroService carroService;

    private Carro carro;
    private CarroDTO carroDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        carro = new Carro();
        carro.setIdCarro(1);
        carro.setPlaca("abc1234");
        carro.setDtAdicionado(new Timestamp(System.currentTimeMillis()));
        carro.setKmAdicionado(12345.0);

        carroDTO = new CarroDTO();
        carroDTO.setIdCarro(1);
        carroDTO.setPlaca("abc1234");
        carroDTO.setDtAdicionado(new Timestamp(System.currentTimeMillis()));
        carroDTO.setKmAdicionado(12345.0);
    }

    @Test
    void getKmRecente_shouldReturnKm() {
        when(carroRepository.findKmMaisRecenteByIdCarro(1)).thenReturn(12345.0);

        Double km = carroService.getKmRecente(1);

        assertEquals(12345.0, km);
        verify(carroRepository).findKmMaisRecenteByIdCarro(1);
    }

    @Test
    void salvarCarro_shouldSaveCarroWhenNotExists() {
        when(carroRepository.existsByPlaca("abc1234")).thenReturn(false);
        when(carroMapper.toEntity(carroDTO)).thenReturn(carro);
        when(carroRepository.save(carro)).thenReturn(carro);
        when(carroMapper.toDTO(carro)).thenReturn(carroDTO);

        carroService.salvarCarro(carroDTO);

        verify(carroRepository).existsByPlaca("abc1234");
        verify(carroRepository).save(any(Carro.class));
    }

    @Test
    void salvarCarro_shouldThrowExceptionWhenPlacaExists() {
        when(carroRepository.existsByPlaca("abc1234")).thenReturn(true);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            carroService.salvarCarro(carroDTO);
        });

        assertEquals("Carro com essa placa já existente!", thrown.getMessage());
        verify(carroRepository).existsByPlaca("abc1234");
        verify(carroRepository, never()).save(any());
    }
    
    @Test
    void salvarCarro_shouldConvertPlacaToLowerCaseAndSetTimestamp() {
        carroDTO.setPlaca("ABC1234"); // Maiúsculo proposital
        carroDTO.setDtAdicionado(null); // Para forçar o set

        when(carroRepository.existsByPlaca("abc1234")).thenReturn(false);
        when(carroMapper.toEntity(any())).thenReturn(carro);
        when(carroRepository.save(any())).thenReturn(carro);
        when(carroMapper.toDTO(any())).thenReturn(carroDTO);

        carroService.salvarCarro(carroDTO);

        assertEquals("abc1234", carroDTO.getPlaca());
        assertNotNull(carroDTO.getDtAdicionado());

        verify(carroRepository).save(any());
    }
    
    @Test
    void deleteCarro_shouldDeleteAbastAndRevisaoAndCarro() {
        Integer idCarro = 1;

        carroService.deleteCarro(idCarro);

        verify(abastRepository).deleteByCarro_IdCarro(idCarro);
        verify(revisaoRepository).deleteByCarro_IdCarro(idCarro);
        verify(carroRepository).deleteById(idCarro);
    }
}
