package com.revisao.demo.service;

import com.revisao.demo.dto.RegisterDTO;
import com.revisao.demo.mapper.EmpresaMapper;
import org.springframework.stereotype.Service;


import com.revisao.demo.dto.UserDTO;
import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.mapper.UserMapper;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.UserRepository;

import javax.naming.CannotProceedException;
import java.util.List;

@Service
public class UserService extends BaseServiceImpl<UserDTO, User, Integer>{

	private final UserRepository repository;

    private final EmpresaService empresaService;

    private final EmpresaMapper empresaMapper;

    private final AuthService authService;
	
    public UserService(UserRepository repository, UserMapper mapper, EmpresaService empresaService, AuthService authService, EmpresaMapper empresaMapper) {
        super(repository, mapper);
        this.repository=repository;
        this.empresaService=empresaService;
        this.authService=authService;
        this.empresaMapper=empresaMapper;
    }
    
    public void updateUserRole(Integer id, UserRoles newRole, Integer empresaId) throws CannotProceedException {
        User user = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if(!user.getEmpresa().getId().equals(empresaId)) throw new CannotProceedException("Impossível realizar solicitação para empresa diferente.");
        user.setRoles(newRole); 
        repository.save(user); 
    }

    public List<UserDTO> getUsersByEmpresa(Integer empresaId) {
        return mapper.toDTOList(repository.findByEmpresaId(empresaId));
    }

    public void saveUser(UserDTO dto, Integer empresaId){
        dto.setEmpresa(empresaService.setEmpresaDTO(empresaId));
        authService.registerUser(toRegisterDTO(dto));
    }

    public RegisterDTO toRegisterDTO(UserDTO dto){
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setCpf(dto.getCpf());
        registerDTO.setNome(dto.getNome());
        registerDTO.setSenha(dto.getSenha());
        registerDTO.setRoles(UserRoles.USER);
        registerDTO.setEmpresa(empresaMapper.toEntity(dto.getEmpresa()));
        return registerDTO;
    }

    
}
