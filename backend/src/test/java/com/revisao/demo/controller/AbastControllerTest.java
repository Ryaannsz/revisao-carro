package com.revisao.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.revisao.demo.dto.AbastDTO;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.User;
import com.revisao.demo.service.AbastService;

//@WebMvcTest(AbastController.class)
@AutoConfigureMockMvc
@SpringBootTest
class AbastControllerTest {

    @Autowired
    private MockMvc mockMvc;

	@MockBean
    private AbastService abastService;

    private AbastDTO abastDTO;

    @BeforeEach
    void setup() {
        User user = new User();
        Carro carro = new Carro();
        carro.setIdCarro(1);

        abastDTO = new AbastDTO(1, 50.0, 200.0, 10000.0, new Timestamp(System.currentTimeMillis()), user, carro);
    }

    @Test
    void getAllAbast_shouldReturnList() throws Exception {
        when(abastService.findAll()).thenReturn(List.of(abastDTO));

        mockMvc.perform(get("/abastecimento"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].idAbast").value(abastDTO.getIdAbast()));
    }

    @Test
    void postAbast_shouldReturnCreated() throws Exception {
        String json = """
            {
              "idAbast": 1,
              "litroComb": 50.0,
              "valorComb": 200.0,
              "kmAtual": 10000.0,
              "dtAbast": "2025-05-23T10:00:00.000+0000",
              "user": null,
              "carro": {"idCarro": 1}
            }
            """;

        doReturn(abastDTO).when(abastService).saveAbast(any());

        mockMvc.perform(post("/abastecimento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void getSpecAbast_shouldReturnAbastDTO() throws Exception {
        when(abastService.findById(1)).thenReturn(Optional.of(abastDTO));

        mockMvc.perform(get("/abastecimento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAbast").value(1));
    }

    @Test
    void getSpecAbast_shouldReturnNotFound() throws Exception {
        when(abastService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/abastecimento/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByCarroId_shouldReturnList() throws Exception {
        when(abastService.findAllByCarroId(1)).thenReturn(List.of(abastDTO));

        mockMvc.perform(get("/abastecimento/carro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAbast").value(1));
    }

    @Test
    void deleteAbast_shouldReturnOk() throws Exception {
        doNothing().when(abastService).delete(1);

        mockMvc.perform(delete("/abastecimento/1"))
                .andExpect(status().isOk());
    }
}
