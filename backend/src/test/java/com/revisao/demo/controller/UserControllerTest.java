package com.revisao.demo.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revisao.demo.dto.UserDTO;
import com.revisao.demo.dto.UpdateRoleDTO;
import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    private UserDTO userDTO;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        userDTO = new UserDTO(1, "12345678901", "Ryan", "123456", UserRoles.USER);
    }

    @Test
    void getAll_shouldReturnListOfUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cpf").value("12345678901"))
                .andExpect(jsonPath("$[0].nome").value("Ryan"));

        verify(userService, times(1)).findAll();
    }

    @Test
    void saveUser_shouldReturnCreatedStatus() throws Exception {
        String userJson = objectMapper.writeValueAsString(userDTO);

        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());

        verify(userService, times(1)).save(any(UserDTO.class));
    }

    @Test
    void patchUser_shouldReturnCreatedStatus() throws Exception {
        UpdateRoleDTO updateRoleDTO = new UpdateRoleDTO();
        updateRoleDTO.setRoles(UserRoles.ADMIN);

        String patchJson = objectMapper.writeValueAsString(updateRoleDTO);

        mockMvc.perform(patch("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchJson))
                .andExpect(status().isCreated());

        verify(userService, times(1)).updateUserRole(1, UserRoles.ADMIN);
    }
}
