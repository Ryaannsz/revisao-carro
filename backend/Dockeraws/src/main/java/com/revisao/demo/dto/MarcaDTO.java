package com.revisao.demo.dto;

import com.revisao.demo.models.Empresa;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcaDTO {

    private Integer id;

    @NotNull
    private String marca;

    private EmpresaDTO empresa;
}
