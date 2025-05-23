package com.revisao.demo.controller;

import java.util.List;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revisao.demo.dto.AbastDTO;
import com.revisao.demo.service.AbastService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("abastecimento")
@RequiredArgsConstructor
public class AbastController {
	
	private final AbastService abastService;
	
	@GetMapping
	public List<AbastDTO> getAllAbast(){
		return abastService.findAll();
	}
	
	
	@PostMapping
	public ResponseEntity<Void> postAbast(@Valid @RequestBody AbastDTO abast){
		abastService.saveAbast(abast);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping("/{idAbast}")
	public ResponseEntity<AbastDTO> getSpecAbast(@PathVariable Integer idAbast) {
		return abastService.findById(idAbast)
	            .map(ResponseEntity::ok)
	            .orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@GetMapping("/carro/{idCarro}")
	public List<AbastDTO> getAllByCarroId(@PathVariable Integer idCarro){
		return abastService.findAllByCarroId(idCarro);
	}
	
	@DeleteMapping("/{idAbast}")
	public ResponseEntity<Void> deleteAbast(@PathVariable Integer idAbast){
		abastService.delete(idAbast);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	


}
