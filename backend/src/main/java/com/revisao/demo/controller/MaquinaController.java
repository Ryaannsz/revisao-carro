package com.revisao.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revisao.demo.dto.MaquinaDTO;
import com.revisao.demo.service.MaquinaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("abastecimento")
@RequiredArgsConstructor
public class MaquinaController {

    private final MaquinaService service;

    @GetMapping
    public ResponseEntity<List<MaquinaDTO>> getAll() {
	return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<MaquinaDTO> postMaquina(@RequestBody MaquinaDTO maquina) {
	return ResponseEntity.created(null).body(service.save(maquina));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaquinaDTO> getSpecMaquina(@PathVariable Integer id) {
	return service.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaquina(@PathVariable Integer id) {
	service.delete(id);
	return ResponseEntity.ok().build();
    }

}
