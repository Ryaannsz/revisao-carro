package com.revisao.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.service.CarroService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("carro")
@RequiredArgsConstructor
public class CarroController {
	
	private final CarroService carroService;
	
	@GetMapping
	public List<CarroDTO> getAllCarros(){
		return carroService.findAll();
	}
	
	@PostMapping
	public ResponseEntity<Void> postCarro(@Valid @RequestBody CarroDTO carro){
		carroService.save(carro);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping("/{idCarro}")
	public ResponseEntity<CarroDTO> getSpecRevisao(@PathVariable Integer idCarro) {
	    return carroService.findById(idCarro)
	            .map(ResponseEntity::ok)
	            .orElseGet(() -> ResponseEntity.notFound().build());
	}
	

}
