package com.revisao.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revisao.demo.dto.MarcaDTO;
import com.revisao.demo.service.MarcaService;

@SpringBootTest
@AutoConfigureMockMvc
class MarcaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MarcaService marcaService;

    @Autowired
    private ObjectMapper objectMapper;

    private MarcaDTO marcaDTO;

    @BeforeEach
    void setup() {
        marcaDTO = new MarcaDTO(1, "Fiat");
    }

    @Test
    void getAllMarcas_shouldReturnList() throws Exception {
        when(marcaService.findAll()).thenReturn(List.of(marcaDTO));

        mockMvc.perform(get("/marca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].marca").value("Fiat"));
    }

    @Test
    void postMarca_shouldReturnCreated() throws Exception {
        when(marcaService.salvarMarca(any(MarcaDTO.class))).thenReturn(marcaDTO);

        String json = objectMapper.writeValueAsString(marcaDTO);

        mockMvc.perform(post("/marca")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marca").value("Fiat"));
    }
}
