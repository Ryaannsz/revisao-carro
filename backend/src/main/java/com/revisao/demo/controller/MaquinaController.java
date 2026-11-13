package com.revisao.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
// Atenção: precisa ser o RequestBody do Spring. O do Swagger (io.swagger...) não lê o corpo.
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revisao.demo.dto.LeituraHorimetroDTO;
import com.revisao.demo.dto.MaquinaDTO;
import com.revisao.demo.enums.MaquinaStatus;
import com.revisao.demo.service.MaquinaService;
import com.revisao.demo.utils.AuthUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("maquina")
@RequiredArgsConstructor
public class MaquinaController {

    private final MaquinaService service;

    @GetMapping
    public List<MaquinaDTO> getAll() {
        return service.findAllByEmpresa(AuthUtils.getEmpresaId());
    }

    @PostMapping
    public ResponseEntity<MaquinaDTO> postMaquina(@Valid @RequestBody MaquinaDTO maquina) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvarMaquina(maquina, AuthUtils.getEmpresaId()));
    }

    @GetMapping("/{id}")
    public MaquinaDTO getSpecMaquina(@PathVariable Integer id) {
        return service.findByIdDaEmpresa(id, AuthUtils.getEmpresaId());
    }

    @GetMapping("/{id}/horas/recente")
    public Double getHorasRecentes(@PathVariable Integer id) {
        return service.getHorasRecentes(id, AuthUtils.getEmpresaId());
    }

    @GetMapping("/{id}/horas")
    public List<LeituraHorimetroDTO> getLeituras(@PathVariable Integer id) {
        return service.listarLeituras(id, AuthUtils.getEmpresaId());
    }

    @PostMapping("/{id}/horas")
    public ResponseEntity<LeituraHorimetroDTO> postLeitura(@PathVariable Integer id, @Valid @RequestBody LeituraHorimetroDTO leitura) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registrarLeitura(id, leitura, AuthUtils.getUser(), AuthUtils.getEmpresaId()));
    }

    @PutMapping("/{id}")
    public MaquinaDTO putMaquina(@PathVariable Integer id, @RequestBody MaquinaDTO dados) {
        return service.atualizarMaquina(id, dados, AuthUtils.getEmpresaId());
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<Void> putStatus(@PathVariable Integer id, @RequestBody MaquinaStatus status) {
        service.atualizarStatus(id, status, AuthUtils.getEmpresaId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable Integer id) {
        service.deleteDaEmpresa(id, AuthUtils.getEmpresaId());
        return ResponseEntity.ok().build();
    }
}
