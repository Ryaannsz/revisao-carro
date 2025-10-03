package com.revisao.demo.controller;

import com.revisao.demo.dto.dashboards.DashboardResumoDTO;
import com.revisao.demo.service.DashboardService;
import com.revisao.demo.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService service;

    @GetMapping("/resumo")
    public DashboardResumoDTO resumo() {

        String empresaId = AuthUtils.getEmpresaId();

        return service.resumoDaEmpresa(empresaId);
    }
}
