package com.revisao.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revisao.demo.dto.ModeloDTO;
import com.revisao.demo.service.ModeloService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ModeloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModeloService modeloService;

    @Autowired
    private ObjectMapper objectMapper;

    private ModeloDTO modeloDTO;

    @BeforeEach
    void setup() {
        modeloDTO = new ModeloDTO(1, "Sedan");
    }

    @Test
    void getAllModelos_shouldReturnList() throws Exception {
        when(modeloService.findAll()).thenReturn(List.of(modeloDTO));

        mockMvc.perform(get("/modelo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modelo").value("Sedan"));
    }

    @Test
    void postModelo_shouldReturnCreated() throws Exception {
        when(modeloService.salvarModelo(any(ModeloDTO.class))).thenReturn(modeloDTO);

        String json = objectMapper.writeValueAsString(modeloDTO);

        mockMvc.perform(post("/modelo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.modelo").value("Sedan"));
    }
}
