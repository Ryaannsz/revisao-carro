package com.revisao.demo.seeder;

import com.revisao.demo.enums.UserRoles;
import com.revisao.demo.models.Empresa;
import com.revisao.demo.models.User;
import com.revisao.demo.repository.EmpresaRepository;
import com.revisao.demo.repository.UserRepository;
import com.revisao.demo.service.EmpresaService;
import com.revisao.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BootstrapSeeder implements CommandLineRunner {
    
    private final EmpresaRepository empresaRepository;
    
    private final BootstrapProperties bootstrapProperties;
    
    private final PasswordEncoder passwordEncoder;
    
    private final UserRepository userRepository;
    
    @Override
    public void run(String... args){
        if(userRepository.findById(1).isEmpty()){
            Empresa empresa = new Empresa();
            empresa.setAtivo(true);
            empresa.setCnpj(bootstrapProperties.getCnpjEmpresa());
            empresa.setNome(bootstrapProperties.getNomeEmpresa());

            empresaRepository.saveAndFlush(empresa);
            
            User newUser = new User();
            newUser.setCpf(bootstrapProperties.getCpf());
            newUser.setSenha(passwordEncoder.encode(bootstrapProperties.getSenha()));
            newUser.setNome(bootstrapProperties.getNomeUser());
            newUser.setEmpresa(empresa);
            newUser.setRoles(UserRoles.ADMIN);

            userRepository.saveAndFlush(newUser);

            System.out.println("Empresa Cadastrada com sucesso!");
            System.out.println("Informações do usuário:");
            System.out.println("Cpf: " + newUser.getCpf());
            System.out.println("Senha: " + newUser.getSenha());

        }
    }
    
}
