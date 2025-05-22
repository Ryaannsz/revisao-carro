package com.revisao.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revisao.demo.dto.ModeloDTO;
import com.revisao.demo.service.ModeloService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("modelo")
@RequiredArgsConstructor
public class ModeloController {
	
	private final ModeloService modeloService;
	
	@GetMapping
	public List<ModeloDTO> getAllModelos(){
		return modeloService.findAll();
	}
	
	@PostMapping
	public ResponseEntity<ModeloDTO> postModelo(@Valid @RequestBody ModeloDTO modelo){
		return ResponseEntity.status(HttpStatus.CREATED).body(modeloService.salvarModelo(modelo));
	}

}
