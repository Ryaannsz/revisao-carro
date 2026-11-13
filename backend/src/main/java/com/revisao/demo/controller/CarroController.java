package com.revisao.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.revisao.demo.dto.CarroDTO;
import com.revisao.demo.enums.CarroStatus;
import com.revisao.demo.service.CarroService;
import com.revisao.demo.utils.AuthUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("carro")
@RequiredArgsConstructor
public class CarroController {

	private final CarroService carroService;

	@GetMapping
	public List<CarroDTO> getAllCarros() {
		return carroService.getCarrosByEmpresa(AuthUtils.getEmpresaId());
	}

	@PostMapping
	public ResponseEntity<Void> postCarro(@Valid @RequestBody CarroDTO carro) {
		carroService.salvarCarro(carro, AuthUtils.getEmpresaId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/{idCarro}")
	public CarroDTO getCarro(@PathVariable Integer idCarro) {
		return carroService.findByIdDaEmpresa(idCarro, AuthUtils.getEmpresaId());
	}

	@GetMapping("/{idCarro}/km/recente")
	public Double getKmRecente(@PathVariable Integer idCarro) {
		return carroService.getKmRecente(idCarro, AuthUtils.getEmpresaId());
	}

	@PutMapping("/{idCarro}")
	public CarroDTO putCarro(@PathVariable Integer idCarro, @RequestBody CarroDTO updateData) {
		return carroService.atualizarCarro(idCarro, updateData, AuthUtils.getEmpresaId());
	}

	@DeleteMapping("/{idCarro}")
	public ResponseEntity<Void> deleteCarro(@PathVariable Integer idCarro) {
		carroService.deleteCarro(idCarro, AuthUtils.getEmpresaId());
		return ResponseEntity.ok().build();
	}

	@PutMapping("/status/{idCarro}")
	public ResponseEntity<Void> putCarroStatus(@PathVariable Integer idCarro, @RequestBody CarroStatus status) {
		carroService.updateCarroStatus(idCarro, status, AuthUtils.getEmpresaId());
		return ResponseEntity.ok().build();
	}
}
