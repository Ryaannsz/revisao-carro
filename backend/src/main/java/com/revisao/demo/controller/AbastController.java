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

import com.revisao.demo.dto.AbastDTO;
import com.revisao.demo.service.AbastService;
import com.revisao.demo.utils.AuthUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("abastecimento")
@RequiredArgsConstructor
public class AbastController {

	private final AbastService abastService;

	@GetMapping
	public List<AbastDTO> getAllAbast() {
		return abastService.findAllByEmpresa(AuthUtils.getEmpresaId());
	}

	@PostMapping
	public ResponseEntity<Void> postAbast(@Valid @RequestBody AbastDTO abast) {
		abastService.saveAbast(abast, AuthUtils.getUser(), AuthUtils.getEmpresaId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/{idAbast}")
	public AbastDTO getSpecAbast(@PathVariable Integer idAbast) {
		return abastService.findByIdDaEmpresa(idAbast, AuthUtils.getEmpresaId());
	}

	@GetMapping("/carro/{idCarro}")
	public List<AbastDTO> getAllByCarroId(@PathVariable Integer idCarro) {
		return abastService.findAllByCarroId(idCarro, AuthUtils.getEmpresaId());
	}

	@DeleteMapping("/{idAbast}")
	public ResponseEntity<Void> deleteAbast(@PathVariable Integer idAbast) {
		abastService.deleteDaEmpresa(idAbast, AuthUtils.getEmpresaId());
		return ResponseEntity.ok().build();
	}
}
