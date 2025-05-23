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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.models.Carro;
import com.revisao.demo.models.User;
import com.revisao.demo.service.RevisaoService;

@SpringBootTest
@AutoConfigureMockMvc
class RevisaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RevisaoService revisaoService;

    private RevisaoDTO revisaoDTO;

    @BeforeEach
    void setUp() {
        Carro carro = new Carro();
        carro.setIdCarro(1);

        revisaoDTO = new RevisaoDTO(1, new Timestamp(System.currentTimeMillis()), 15000.0, new User(), carro);
    }

    @Test
    void getAllRevisao_shouldReturnList() throws Exception {
        when(revisaoService.findAll()).thenReturn(List.of(revisaoDTO));

        mockMvc.perform(get("/revisao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRevisao").value(1));
    }

    @Test
    void postRevisao_shouldReturnCreated() throws Exception {
        String json = """
            {
              "idRevisao": 1,
              "kmAtual": 15000.0,
              "carro": { "idCarro": 1 },
              "user": null,
              "dtRevisao": null
            }
            """;

        doReturn(revisaoDTO).when(revisaoService).saveRevisao(any());

        mockMvc.perform(post("/revisao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void getSpecRevisao_shouldReturnFound() throws Exception {
        when(revisaoService.findById(1)).thenReturn(Optional.of(revisaoDTO));

        mockMvc.perform(get("/revisao/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idRevisao").value(1));
    }

    @Test
    void getSpecRevisao_shouldReturnNotFound() throws Exception {
        when(revisaoService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/revisao/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRevisaoByCarroId_shouldReturnList() throws Exception {
        when(revisaoService.listRevisaoByCarroId(1)).thenReturn(List.of(revisaoDTO));

        mockMvc.perform(get("/revisao/carro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idRevisao").value(1));
    }

    @Test
    void deleteRevisao_shouldReturnOk() throws Exception {
        doNothing().when(revisaoService).delete(1);

        mockMvc.perform(delete("/revisao/1"))
                .andExpect(status().isOk());
    }
}
