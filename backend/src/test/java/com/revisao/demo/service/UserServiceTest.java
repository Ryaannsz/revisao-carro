package com.revisao.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import com.revisao.demo.dto.UserDTO;
import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.mapper.UserMapper;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private User userEntity;

    @BeforeEach
    void setup() {
        userDTO = new UserDTO(1, "12345678901", "Ryan", "123456", UserRoles.USER);
        userEntity = new User(1, "Ryan", "12345678901", "123456", UserRoles.USER);
    }

    @Test
    void saveUser_shouldReturnSavedUserDTO() {
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(userEntity);
        when(userRepository.save(any(User.class))).thenReturn(userEntity);
        doReturn(userDTO).when(userMapper).toDTO(any(User.class));

        UserDTO saved = userService.save(userDTO);

        assertNotNull(saved);
        assertEquals(userDTO.getCpf(), saved.getCpf());
        verify(userRepository).save(any(User.class));
    }
    @Test
    void updateUserRole_shouldUpdateRoleSuccessfully() {
        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        userService.updateUserRole(1, UserRoles.ADMIN);

        assertEquals(UserRoles.ADMIN, userEntity.getRoles());
        verify(userRepository).save(userEntity);
    }

    @Test
    void updateUserRole_shouldThrowIfUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.updateUserRole(1, UserRoles.ADMIN));
        assertEquals("Usuário não encontrado", ex.getMessage());
    }
    
    @Test
    void findById_shouldReturnUserDTO() {
        when(userRepository.findById(1)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDTO(userEntity)).thenReturn(userDTO);

        Optional<UserDTO> found = userService.findById(1); 

        assertTrue(found.isPresent());
        assertEquals("12345678901", found.get().getCpf());
    }
    
    @Test
    void deleteUser_shouldDeleteById() {
        userService.delete(1);
        verify(userRepository).deleteById(1);
    }

}