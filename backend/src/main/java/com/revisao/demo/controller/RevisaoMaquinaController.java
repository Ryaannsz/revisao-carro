package com.revisao.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revisao.demo.dto.RevisaoMaquinaDTO;
import com.revisao.demo.service.RevisaoMaquinaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("revisao/maquina")
@RequiredArgsConstructor
public class RevisaoMaquinaController {

    private final RevisaoMaquinaService service;

    @GetMapping
    public ResponseEntity<List<RevisaoMaquinaDTO>> getAll() {
	return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<RevisaoMaquinaDTO> postRevisaoMaquina(@RequestBody RevisaoMaquinaDTO revisaoMaquina) {
	return ResponseEntity.created(null).body(service.save(revisaoMaquina));
    }

}
