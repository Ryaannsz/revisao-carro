package com.revisao.demo.service;

import com.lowagie.text.*;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.revisao.demo.dto.dashboards.CarOptionDTO;
import com.revisao.demo.dto.dashboards.CarStatsResponseDTO;
import com.revisao.demo.dto.dashboards.MesValorDTO;
import com.revisao.demo.dto.dashboards.ProximaRevisaoDTO;
import com.revisao.demo.repository.RevisaoRepository;
import com.revisao.demo.utils.pdf.PdfHeaderFooter;
import com.revisao.demo.utils.pdf.PdfUtils;
import lombok.RequiredArgsConstructor;
import org.knowm.xchart.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

import static com.revisao.demo.utils.pdf.PdfUtils.*;

@RequiredArgsConstructor
@Service
public class PdfService {

    private final CarStatsService carStatsService;
    private final DashboardService dashboardService;
    private final RevisaoRepository revisaoRepository;

    public ByteArrayOutputStream mountPdf(List<Integer> carIds, Integer months, String mode, Integer empresaId) throws Exception {
        var stats = carStatsService.getCarStats(carIds, months, mode, empresaId);
        List<String> placas = stats.getCars().stream().map(CarOptionDTO::getLabel).toList();
        List<ProximaRevisaoDTO> proximas = dashboardService.estimarProximasRevisoes(revisaoRepository.ultimaRevisaoPorCarro(empresaId));
        proximas = proximas.stream().filter( p -> !placas.contains(p.getPlaca())).toList();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 54, 40); // margens: esq, dir, top, bottom
        PdfWriter writer = PdfWriter.getInstance(doc, baos);

        // metadados
        doc.addTitle("Relatório da Dashboard");
        doc.addAuthor("Seu Sistema");
        doc.addSubject("Relatório PDF");
        writer.setPageEvent(new PdfHeaderFooter("Relatório · Dashboard"));

        doc.open();
        addCover(doc, placas, months, mode);
        addKpis(doc, stats);
        addChartsSection(doc, stats);
        addProximasTable(doc, proximas);
        doc.close();
        return baos;
    }

    private void addSeries(CategoryChart chart, CarStatsResponseDTO stats, String key) {
        // se modo == sum → usa seriesBlock.sum; se compare → cria 1 série por carro
        final var monthsYm = stats.getLabels(); // ex.: ["2025-01", "2025-02", ...]
        // labels formatados para exibição: "Jan", "Fev" ... ou "Jan/25" se houver múltiplos anos
        final var labelsPretty = formatMonthLabels(monthsYm);

        switch (key) {
            case "gasto" -> {
                if ("sum".equals(stats.getMode())) {
                    var y = toCategoryYAligned(monthsYm, stats.getGasto().getSum());
                    chart.addSeries("Soma", labelsPretty, y);
                } else {
                    stats.getGasto().getByCar().entrySet().stream()
                            .sorted(java.util.Map.Entry.comparingByKey())
                            .forEach(e -> {
                                var carId = e.getKey();
                                var arr   = e.getValue();
                                var y     = toCategoryYAligned(monthsYm, arr);
                                chart.addSeries(labelDoCarro(stats, carId), labelsPretty, y);
                            });
                }
            }
            case "litros" -> {
                if ("sum".equals(stats.getMode())) {
                    var y = toCategoryYAligned(monthsYm, stats.getLitros().getSum());
                    chart.addSeries("Soma", labelsPretty, y);
                } else {
                    stats.getLitros().getByCar().entrySet().stream()
                            .sorted(java.util.Map.Entry.comparingByKey())
                            .forEach(e -> {
                                var carId = e.getKey();
                                var arr   = e.getValue();
                                var y     = toCategoryYAligned(monthsYm, arr);
                                chart.addSeries(labelDoCarro(stats, carId), labelsPretty, y);
                            });
                }
            }
            case "eficiencia" -> {
                if ("sum".equals(stats.getMode())) {
                    var y = toCategoryYAligned(monthsYm, stats.getEficiencia().getSum());
                    chart.addSeries("Média ponderada", labelsPretty, y);
                } else {
                    stats.getEficiencia().getByCar().entrySet().stream()
                            .sorted(java.util.Map.Entry.comparingByKey())
                            .forEach(e -> {
                                var carId = e.getKey();
                                var arr   = e.getValue();
                                var y     = toCategoryYAligned(monthsYm, arr);
                                chart.addSeries(labelDoCarro(stats, carId), labelsPretty, y);
                            });
                }
            }
        }

        // ajustes visuais recomendados
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setLegendPadding(6);
        chart.getStyler().setPlotContentSize(0.95);
        chart.getStyler().setXAxisLabelRotation(0);
        chart.getStyler().setAvailableSpaceFill(0.9);
    }

    private com.lowagie.text.Image toPdfImage(BufferedImage bi, float maxWidth) throws Exception {
        var img = com.lowagie.text.Image.getInstance(bi, null);
        if (img.getScaledWidth() > maxWidth) {
            float scale = maxWidth / img.getScaledWidth();
            img.scalePercent(scale * 100f);
        }
        return img;
    }

    private void addHeader(PdfPTable t, String... cols) {
        for (var c : cols) {
            var cell = new PdfPCell(new Phrase(c, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
            cell.setBackgroundColor(new Color(240,240,240));
            t.addCell(cell);
        }
    }
    private void addRow(PdfPTable t, String... cols) { for (var c : cols) t.addCell(c); }

    private String labelDoCarro(CarStatsResponseDTO s, Integer id) {
        return s.getCars().stream().filter(c -> c.getId().equals(id)).findFirst().map(CarOptionDTO::getLabel).orElse("#"+id);
    }

    private java.util.List<String> formatMonthLabels(java.util.List<String> ym) {
        // detecta múltiplos anos
        var years = ym.stream()
                .map(s -> s.split("-")[0])
                .distinct()
                .toList();
        boolean multiYear = years.size() > 1;

        var locale = new java.util.Locale("pt", "BR");
        var out = new java.util.ArrayList<String>(ym.size());
        for (var s : ym) {
            try {
                var parts = s.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                var ymObj = java.time.YearMonth.of(year, month);
                var fmt = java.time.format.DateTimeFormatter.ofPattern(multiYear ? "MMM/yy" : "MMM", locale);
                out.add(ymObj.atDay(1).format(fmt));
            } catch (Exception ex) {
                // fallback seguro se vier algo fora do padrão:
                out.add(s);
            }
        }
        return out;
    }

    /** Alinha a série (MesValorDTO) à lista de meses YYYY-MM, substituindo null por 0 (CategoryChart requer Number). */
    private java.util.List<Number> toCategoryYAligned(
            java.util.List<String> monthsYm,
            java.util.List<MesValorDTO> points
    ) {
        var map = new java.util.HashMap<String, java.lang.Double>(points.size() * 2);
        for (var p : points) {
            // p.getMes() deve ser "YYYY-MM"
            map.put(p.getMes(), p.getValor());
        }
        var y = new java.util.ArrayList<Number>(monthsYm.size());
        for (var m : monthsYm) {
            var v = map.get(m);
            y.add(v == null ? 0.0 : v); // CategoryChart não suporta null/NaN; usamos 0 por estabilidade
        }
        return y;
    }

    private void addCover(Document doc, List<String> placas, int months, String mode) throws Exception {
        Paragraph title = new Paragraph("Relatório da Dashboard", H1);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(new Paragraph(" "));

        PdfPTable meta = new PdfPTable(2);
        meta.setWidthPercentage(100);
        meta.setWidths(new float[]{1.2f, 3f});

        meta.addCell(PdfUtils.headerCell("Carros"));
        meta.addCell(PdfUtils.cell(String.join(", ", placas), TXT, Element.ALIGN_LEFT));
        meta.addCell(PdfUtils.headerCell("Período"));
        meta.addCell(PdfUtils.cell(months <= 0 ? "Todos os meses" : "Últimos " + months + " meses", TXT, Element.ALIGN_LEFT));
        meta.addCell(PdfUtils.headerCell("Modo"));
        meta.addCell(PdfUtils.cell("compare".equalsIgnoreCase(mode) ? "Comparar" : "Somar", TXT, Element.ALIGN_LEFT));

        doc.add(meta);
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph("Relatório gerado automaticamente a partir dos dados do painel.", SMALL_MUTED));
        doc.newPage();
    }

    private void addKpis(Document doc, CarStatsResponseDTO stats){
        // exemplo simples: total gasto somado, litros somados, eficiência média do período
        double totalGasto = stats.getGasto().getSum().stream().map(MesValorDTO::getValor).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
        double totalLitros = stats.getLitros().getSum().stream().map(MesValorDTO::getValor).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
        Double mediaEf = stats.getEficiencia().getSum().stream().map(MesValorDTO::getValor).filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue).average().orElse(Double.NaN);

        PdfPTable kpis = new PdfPTable(3);
        kpis.setWidthPercentage(100);
        kpis.setSpacingBefore(4f);
        kpis.setWidths(new float[]{1,1,1});

        PdfPCell c1 = new PdfPCell(); c1.setPadding(10); c1.setBackgroundColor(Color.WHITE); c1.setBorderColor(SLATE_BORDER);
        c1.addElement(kpiTitle("Gasto (período)"));
        c1.addElement(kpiValue(CURRENCY.format(totalGasto)));

        PdfPCell c2 = new PdfPCell(); c2.setPadding(10); c2.setBackgroundColor(Color.WHITE); c2.setBorderColor(SLATE_BORDER);
        c2.addElement(kpiTitle("Litros (período)"));
        c2.addElement(kpiValue(NUM.format(totalLitros)));

        PdfPCell c3 = new PdfPCell(); c3.setPadding(10); c3.setBackgroundColor(Color.WHITE); c3.setBorderColor(SLATE_BORDER);
        c3.addElement(kpiTitle("Eficiência média"));
        c3.addElement(kpiValue(Double.isNaN(mediaEf) ? "s/dado" : NUM.format(mediaEf) + " km/L"));

        kpis.addCell(c1); kpis.addCell(c2); kpis.addCell(c3);
        doc.add(new Paragraph("Resumo do período", H2));
        doc.add(kpis);
        doc.add(new Paragraph(" "));
    }

    private CategoryChart buildCategoryChart(String titulo, String yTitle){
        CategoryChart ch = new CategoryChartBuilder()
                .width(1100).height(420)
                .title(titulo).xAxisTitle("Mês").yAxisTitle(yTitle).build();
        ch.getStyler().setLegendVisible(true);
        ch.getStyler().setAvailableSpaceFill(0.9);
        ch.getStyler().setPlotContentSize(0.95);
        ch.getStyler().setXAxisLabelRotation(0);
        return ch;
    }


    private void addChartsSection(Document doc, CarStatsResponseDTO stats) throws Exception {
        // página horizontal para caber dois charts
        Document sub = doc;
        Rectangle land = PageSize.A4.rotate();
        sub.setPageSize(land);
        sub.newPage();

        doc.add(new Paragraph("Gráficos", H2));

        CategoryChart gasto = buildCategoryChart("Gasto mensal (R$)", "R$");
        addSeries(gasto, stats, "gasto");
        CategoryChart litros = buildCategoryChart("Litros por mês (L)", "L");
        addSeries(litros, stats, "litros");

        com.lowagie.text.Image imgGasto  = toPdfImage(BitmapEncoder.getBufferedImage(gasto), 760);
        com.lowagie.text.Image imgLitros = toPdfImage(BitmapEncoder.getBufferedImage(litros), 760);
        doc.add(imgGasto);
        doc.add(Chunk.NEWLINE);
        doc.add(imgLitros);

        doc.newPage();
        CategoryChart efic = buildCategoryChart("Eficiência (km/L)", "km/L");
        addSeries(efic, stats, "eficiencia");
        com.lowagie.text.Image imgEfic = toPdfImage(BitmapEncoder.getBufferedImage(efic), 760);
        doc.add(imgEfic);
    }

    private void addProximasTable(Document doc, List<ProximaRevisaoDTO> proximas) throws Exception {
        if (proximas == null || proximas.isEmpty()) return;

        doc.add(new Paragraph("Próximas revisões", H2));
        PdfPTable t = new PdfPTable(new float[]{1.2f, 1f, 1.2f, 0.8f});
        t.setWidthPercentage(100);

        t.addCell(headerCell("Placa"));
        t.addCell(headerCell("Última"));
        t.addCell(headerCell("Próxima (estimada)"));
        t.addCell(headerCell("Km atual"));

        java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");
        boolean zebra = false;
        for (var p : proximas) {
            Color bg = zebra ? new Color(250,250,252) : Color.WHITE; zebra = !zebra;

            PdfPCell c1 = cell(p.getPlaca(), TXT, Element.ALIGN_LEFT); c1.setBackgroundColor(bg); t.addCell(c1);
            PdfPCell c2 = cell(p.getUltimaRevisao() == null ? "-" : df.format(p.getUltimaRevisao()), TXT, Element.ALIGN_LEFT); c2.setBackgroundColor(bg); t.addCell(c2);
            PdfPCell c3 = cell(p.getProximaRevisaoEstimada() == null ? "-" : df.format(p.getProximaRevisaoEstimada()), TXT, Element.ALIGN_LEFT); c3.setBackgroundColor(bg); t.addCell(c3);
            PdfPCell c4 = cell(p.getKmAtualEstimado() == null ? "-" : NUM.format(p.getKmAtualEstimado()), TXT, Element.ALIGN_RIGHT); c4.setBackgroundColor(bg); t.addCell(c4);
        }
        doc.add(t);
    }


}
