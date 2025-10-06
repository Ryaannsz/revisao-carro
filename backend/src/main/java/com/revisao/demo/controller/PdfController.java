package com.revisao.demo.controller;

import com.revisao.demo.service.PdfService;
import com.revisao.demo.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("pdf/dashboard")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    @GetMapping(value = "/caroption/report.pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> reportCarOptionPdf(@RequestParam(required = false, defaultValue = "6") Integer months,
                                                     @RequestParam List<Integer> carIds,
                                                     @RequestParam(required = false, defaultValue = "compare") String mode) throws Exception{
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-dashboard.pdf")
                .body(pdfService.mountPdf(carIds, months, mode, AuthUtils.getEmpresaId()).toByteArray());

    }

}
