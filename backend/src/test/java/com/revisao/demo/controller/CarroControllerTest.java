package com.revisao.demo.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.service.CarroService;

@SpringBootTest
@AutoConfigureMockMvc
class CarroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarroService carroService;

    @Autowired
    private ObjectMapper objectMapper;

    private CarroDTO carroDTO;

    @BeforeEach
    void setup() {
        carroDTO = new CarroDTO();
        carroDTO.setIdCarro(1);
        carroDTO.setPlaca("abc1234");
        carroDTO.setKmAdicionado(12345.0);
        carroDTO.setDtAdicionado(new Timestamp(System.currentTimeMillis()));
    }

    @Test
    void getAllCarros_shouldReturnList() throws Exception {
        when(carroService.findAll()).thenReturn(List.of(carroDTO));

        mockMvc.perform(get("/carro"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].idCarro").value(carroDTO.getIdCarro()))
               .andExpect(jsonPath("$[0].placa").value(carroDTO.getPlaca()));
    }

    @Test
    void postCarro_shouldReturnCreated() throws Exception {
        doNothing().when(carroService).salvarCarro(any());

        mockMvc.perform(post("/carro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carroDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void getSpecCarro_shouldReturnCarro() throws Exception {
        when(carroService.findById(1)).thenReturn(Optional.of(carroDTO));

        mockMvc.perform(get("/carro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCarro").value(1))
                .andExpect(jsonPath("$.placa").value(carroDTO.getPlaca()));
    }

    @Test
    void getSpecCarro_shouldReturnNotFound() throws Exception {
        when(carroService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/carro/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getKmRecente_shouldReturnKm() throws Exception {
        when(carroService.getKmRecente(1)).thenReturn(12345.0);

        mockMvc.perform(get("/carro/1/km/recente"))
                .andExpect(status().isOk())
                .andExpect(content().string("12345.0"));
    }

    @Test
    void putCarro_shouldReturnOk() throws Exception {
    	when(carroService.update(any(), any())).thenReturn(carroDTO);

        mockMvc.perform(put("/carro/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carroDTO)))
                .andExpect(status().isOk());
    }
}
