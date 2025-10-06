package com.revisao.demo.controller;

import com.revisao.demo.dto.dashboards.CarOptionDTO;
import com.revisao.demo.dto.dashboards.CarStatsResponseDTO;
import com.revisao.demo.service.CarStatsService;
import com.revisao.demo.service.CarroService;
import com.revisao.demo.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/car-stats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CarStatsController {

    private final CarStatsService service;

    private final CarroService carroService;

    @GetMapping()
    public CarStatsResponseDTO carStats(
            @RequestParam("carIds") String carIdsCsv,
            @RequestParam(name="months", required=false) Integer months,
            @RequestParam(name="mode", required=false) String mode
    ) {
        List<Integer> carIds = Arrays.stream(carIdsCsv.split(","))
                .filter(s -> !s.isBlank())
                .map(Integer::valueOf)
                .toList();

        return service.getCarStats(carIds, months, mode, AuthUtils.getEmpresaId());
    }
@GetMapping("/carros")
public List<CarOptionDTO> getCarrosByEmpresa(Integer empresaId) {
        return carroService.getCarOptions(AuthUtils.getEmpresaId());
}


}
