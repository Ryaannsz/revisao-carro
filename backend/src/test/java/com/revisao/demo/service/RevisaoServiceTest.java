package com.revisao.demo.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.dto.UserDTO;
import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.mapper.RevisaoMapper;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.Revisao;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.RevisaoRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RevisaoServiceTest {

    @Mock
    private RevisaoRepository revisaoRepository;

    @Mock
    private RevisaoMapper revisaoMapper;

    @Mock
    private CarroService carroService;

    @InjectMocks
    private RevisaoService revisaoService;

    private RevisaoDTO revisaoDTO;
    private Carro carro;
    private User user;

    @BeforeEach
    void setUp() {
        carro = new Carro();
        carro.setIdCarro(1);

        revisaoDTO = new RevisaoDTO(null, null, 12000.0, new User(), carro);
        
        user = new User(1, "Jhon", "wodmwd", "wdlawdml", UserRoles.ADMIN);
    }

    @Test
    void saveRevisao_shouldSaveSuccessfully() {
        RevisaoDTO revisaoDTO = new RevisaoDTO(null, null, 15000.0, user, carro);
        Revisao revisaoEntity = new Revisao(null, null, 15000.0, user, carro);

        when(carroService.getKmRecente(carro.getIdCarro())).thenReturn(10000.0);
        when(revisaoMapper.toEntity(any())).thenReturn(revisaoEntity);
        when(revisaoRepository.save(any())).thenReturn(revisaoEntity);
        when(revisaoMapper.toDTO(any())).thenReturn(revisaoDTO);

        RevisaoDTO result = revisaoService.saveRevisao(revisaoDTO);

        assertNotNull(result); 
        assertEquals(15000.0, result.getKmAtual());
    }

    @Test
    void saveRevisao_shouldThrowExceptionIfKmIsLower() {
        when(carroService.getKmRecente(1)).thenReturn(13000.0);

        assertThrows(IllegalArgumentException.class, () -> revisaoService.saveRevisao(revisaoDTO));
    }
    
    @Test
    void saveRevisao_shouldSetCurrentTimestampIfNull() {
        revisaoDTO.setKmAtual(15000.0);
        revisaoDTO.setDtRevisao(null);  // deixar nulo para validar atribuição
        revisaoDTO.setUser(user);

        Revisao revisaoEntity = new Revisao(null, null, 15000.0, user, carro);
        Revisao revisaoSaved = new Revisao(1, new Timestamp(System.currentTimeMillis()), 15000.0, user, carro);

        when(carroService.getKmRecente(carro.getIdCarro())).thenReturn(10000.0);
        when(revisaoMapper.toEntity(any())).thenReturn(revisaoEntity);
        when(revisaoRepository.save(any())).thenReturn(revisaoSaved);
        when(revisaoMapper.toDTO(any())).thenReturn(revisaoDTO);

        RevisaoDTO result = revisaoService.saveRevisao(revisaoDTO);

        assertNotNull(result.getDtRevisao(), "A data de revisão deveria ser setada automaticamente");
    }
    
    @Test
    void listRevisaoByCarroId_shouldReturnList() {
        Revisao revisao = new Revisao(1, new Timestamp(System.currentTimeMillis()), 20000.0, user, carro);
        RevisaoDTO revisaoDTO = new RevisaoDTO(1, new Timestamp(System.currentTimeMillis()), 20000.0, user, carro);

        when(revisaoRepository.findByCarroIdCarro(carro.getIdCarro())).thenReturn(List.of(revisao));
        when(revisaoMapper.toDTOList(List.of(revisao))).thenReturn(List.of(revisaoDTO));

        List<RevisaoDTO> result = revisaoService.listRevisaoByCarroId(carro.getIdCarro());

        assertEquals(1, result.size());
        assertEquals(20000.0, result.get(0).getKmAtual());
    }
    
    @Test
    void saveRevisao_shouldPropagateExceptionFromRepository() {
        revisaoDTO.setKmAtual(20000.0);
        revisaoDTO.setUser(user);

        when(carroService.getKmRecente(carro.getIdCarro())).thenReturn(10000.0);
        when(revisaoMapper.toEntity(any())).thenReturn(new Revisao(null, null, 20000.0, user, carro));
        when(revisaoRepository.save(any())).thenThrow(new RuntimeException("Erro interno"));

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> revisaoService.saveRevisao(revisaoDTO));

        assertEquals("Erro interno", exception.getMessage());
    }

    
    

}
