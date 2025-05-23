package com.revisao.demo.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.revisao.demo.dto.UserDTO;
import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.models.User;

public class UserMapperTest {

    private UserMapper mapper = Mappers.getMapper(UserMapper.class);

    private User entity;
    private UserDTO dto;
    
    
    
    @BeforeEach
    void seTup(){
    	dto = new UserDTO();
    	dto.setCpf("12345678900");
    	dto.setNome("Ryan");
    	dto.setIdUser(1);
    	dto.setRoles(UserRoles.ADMIN);
    	
    	entity = new User();
    	entity=mapper.toEntity(dto);
    }
 

    @Test
    void testToEntity() {


        assertNotNull(entity);
        assertEquals(dto.getIdUser(), entity.getIdUser());
        assertEquals(dto.getNome(), entity.getNome());
        assertEquals(dto.getCpf(), entity.getCpf());
        assertEquals(dto.getRoles(), entity.getRoles());
        assertNull(entity.getSenha()); // senha não é mapeada pelo DTO
    }

    @Test
    void testToDTO() {
        User entity = new User();
        entity.setIdUser(2);
        entity.setNome("Carla");
        entity.setCpf("98765432100");
        entity.setRoles(UserRoles.USER);
        entity.setSenha("1234");

        UserDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals(entity.getIdUser(), dto.getIdUser());
        assertEquals(entity.getNome(), dto.getNome());
        assertEquals(entity.getCpf(), dto.getCpf());
        assertEquals(entity.getRoles(), dto.getRoles());
    }

    @Test
    void testToDTOList() {
        User u1 = new User();
        u1.setIdUser(1);
        u1.setNome("Ana");
        u1.setCpf("11111111111");
        u1.setRoles(UserRoles.ADMIN);

        User u2 = new User();
        u2.setIdUser(2);
        u2.setNome("Bruno");
        u2.setCpf("22222222222");
        u2.setRoles(UserRoles.USER);

        List<User> users = Arrays.asList(u1, u2);

        List<UserDTO> dtos = mapper.toDTOList(users);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("Ana", dtos.get(0).getNome());
        assertEquals("Bruno", dtos.get(1).getNome());
    }

    @Test
    void testToEntityList() {
        UserDTO dto1 = new UserDTO();
        dto1.setCpf("33333333333");
        dto1.setNome("Lucas");
        dto.setIdUser(3);
        dto.setRoles(UserRoles.USER);
        
        UserDTO dto2 = new UserDTO();
        dto2.setCpf("44444444444");
        dto2.setNome("Mariana");
        dto2.setIdUser(4);
        dto2.setRoles(UserRoles.ADMIN);

        List<UserDTO> dtos = Arrays.asList(dto1, dto2);

        List<User> users = mapper.toEntityList(dtos);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("Lucas", users.get(0).getNome());
        assertEquals("Mariana", users.get(1).getNome());
    }

    // Testes para métodos UserDetails

    @Test
    void testGetAuthoritiesForAdmin() {
        User admin = new User();
        admin.setRoles(UserRoles.ADMIN);

        Collection<? extends GrantedAuthority> authorities = admin.getAuthorities();

        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testGetAuthoritiesForUser() {
        User user = new User();
        user.setRoles(UserRoles.USER);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void testGetPasswordAndUsername() {
        User user = new User();
        user.setSenha("senhaSecreta");
        user.setCpf("99988877766");

        assertEquals("senhaSecreta", user.getPassword());
        assertEquals("99988877766", user.getUsername());
    }
}
