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

import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.service.RevisaoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("revisao")
@RequiredArgsConstructor
public class RevisaoController {

	private final RevisaoService revisaoService;

	@GetMapping
	public List<RevisaoDTO> getAllRevisao() {
		return revisaoService.findAll();
	}

	@PostMapping
	public ResponseEntity<Void> postRevisao(@Valid @RequestBody RevisaoDTO revisao) {
		revisaoService.saveRevisao(revisao);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping("/{idRevisao}")
	public ResponseEntity<RevisaoDTO> getSpecRevisao(@PathVariable Integer idRevisao) {
	    return revisaoService.findById(idRevisao)
	            .map(ResponseEntity::ok)
	            .orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@GetMapping("/carro/{idRevisao}")
	public List<RevisaoDTO> getRevisaoByCarroId(@PathVariable Integer idRevisao){
		return revisaoService.listRevisaoByCarroId(idRevisao);
	}
	
	@DeleteMapping("/{idRevisao}")
	public ResponseEntity<Void> deleteRevisao(@PathVariable Integer idRevisao){
		revisaoService.delete(idRevisao);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
