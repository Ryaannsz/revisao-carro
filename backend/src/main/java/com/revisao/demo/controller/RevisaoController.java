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

import com.revisao.demo.dto.RevisaoDTO;
import com.revisao.demo.dto.dashboards.ProximaRevisaoDTO;
import com.revisao.demo.service.RevisaoService;
import com.revisao.demo.utils.AuthUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("revisao")
@RequiredArgsConstructor
public class RevisaoController {

	private final RevisaoService revisaoService;

	@GetMapping
	public List<RevisaoDTO> getAllRevisao() {
		return revisaoService.findAllByEmpresa(AuthUtils.getEmpresaId());
	}

	@PostMapping
	public ResponseEntity<Void> postRevisao(@Valid @RequestBody RevisaoDTO revisao) {
		revisaoService.saveRevisao(revisao, AuthUtils.getUser(), AuthUtils.getEmpresaId());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/{idRevisao:\\d+}")
	public RevisaoDTO getSpecRevisao(@PathVariable Integer idRevisao) {
		return revisaoService.findByIdDaEmpresa(idRevisao, AuthUtils.getEmpresaId());
	}

	@GetMapping("/carro/{idCarro}")
	public List<RevisaoDTO> getRevisaoByCarroId(@PathVariable Integer idCarro) {
		return revisaoService.listRevisaoByCarroId(idCarro, AuthUtils.getEmpresaId());
	}

	@DeleteMapping("/{idRevisao:\\d+}")
	public ResponseEntity<Void> deleteRevisao(@PathVariable Integer idRevisao) {
		revisaoService.deleteDaEmpresa(idRevisao, AuthUtils.getEmpresaId());
		return ResponseEntity.ok().build();
	}

	@GetMapping("/alarme")
	public List<ProximaRevisaoDTO> alarmeRevisao() {
		return revisaoService.calcularProxRevisao(AuthUtils.getEmpresaId());
	}

	@GetMapping("/alarme/{carroId}")
	public ProximaRevisaoDTO alarmeRevisaoPorCarro(@PathVariable Integer carroId) {
		return revisaoService.calcularProxRevisao(carroId, AuthUtils.getEmpresaId());
	}
}
