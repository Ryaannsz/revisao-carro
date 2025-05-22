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

import com.revisao.demo.dto.MarcaDTO;
import com.revisao.demo.models.Marca;
import com.revisao.demo.service.MarcaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("marca")
@RequiredArgsConstructor
public class MarcaController {
	
	private final MarcaService marcaService;
	
	@GetMapping
	public List<MarcaDTO> getAllMarcas(){
		return marcaService.findAll();
	}
	
	@PostMapping
	public ResponseEntity<MarcaDTO> postMarca(@Valid @RequestBody MarcaDTO marca){
		return ResponseEntity.status(HttpStatus.CREATED).body(marcaService.salvarMarca(marca));	
	}

}
