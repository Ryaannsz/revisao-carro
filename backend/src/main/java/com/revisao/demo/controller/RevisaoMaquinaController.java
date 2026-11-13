package com.revisao.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revisao.demo.dto.RevisaoMaquinaDTO;
import com.revisao.demo.dto.dashboards.ProximaRevisaoMaquinaDTO;
import com.revisao.demo.service.RevisaoMaquinaService;
import com.revisao.demo.utils.AuthUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("revisao/maquina")
@RequiredArgsConstructor
public class RevisaoMaquinaController {

    private final RevisaoMaquinaService service;

    @GetMapping
    public List<RevisaoMaquinaDTO> getAll() {
        return service.findAllByEmpresa(AuthUtils.getEmpresaId());
    }

    @GetMapping("/maquina/{maquinaId}")
    public List<RevisaoMaquinaDTO> getByMaquina(@PathVariable Integer maquinaId) {
        return service.findAllByMaquina(maquinaId, AuthUtils.getEmpresaId());
    }

    @GetMapping("/alarme")
    public List<ProximaRevisaoMaquinaDTO> alarme() {
        return service.calcularProxRevisao(AuthUtils.getEmpresaId());
    }

    @GetMapping("/alarme/{maquinaId}")
    public ProximaRevisaoMaquinaDTO alarmePorMaquina(@PathVariable Integer maquinaId) {
        return service.calcularProxRevisao(maquinaId, AuthUtils.getEmpresaId());
    }

    @PostMapping
    public ResponseEntity<RevisaoMaquinaDTO> postRevisaoMaquina(@Valid @RequestBody RevisaoMaquinaDTO revisaoMaquina) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.salvar(revisaoMaquina, AuthUtils.getUser(), AuthUtils.getEmpresaId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.deleteDaEmpresa(id, AuthUtils.getEmpresaId());
        return ResponseEntity.ok().build();
    }
}
