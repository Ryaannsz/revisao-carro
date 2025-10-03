package com.revisao.demo.seeder;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class BootstrapProperties {

    @Value("${bootstrap.empresa.nome}")
    private String nomeEmpresa;
    @Value("${bootstrap.empresa.cnpj}")
    private String cnpjEmpresa;
    private String dataExpiracao;
    private Boolean ativoEmpresa;

    @Value("${bootstrap.user.senha}")
    private String senha;
    @Value("${bootstrap.user.nome}")
    private String nomeUser;
    @Value("${bootstrap.user.cpf}")
    private String cpf;

}
