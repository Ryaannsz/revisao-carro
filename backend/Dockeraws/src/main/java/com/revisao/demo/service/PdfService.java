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
import com.revisao.demo.utils.pdf.BrandedPageEvent;

import com.revisao.demo.utils.pdf.ProgressBarCell;
import lombok.RequiredArgsConstructor;
import org.knowm.xchart.*;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


@RequiredArgsConstructor
@Service
public class PdfService {

    private final CarStatsService carStatsService;

    private final RevisaoRepository revisaoRepository;

    private final RevisaoService revisaoService;

    private final DashboardService dashboardService;

    public ByteArrayOutputStream mountPdf(List<Integer> carIds, Integer months, String mode, Integer empresaId) throws Exception {
        var stats = carStatsService.getCarStats(carIds, months, mode, empresaId);
        List<String> placas = stats.getCars().stream().map(CarOptionDTO::getLabel).toList();
        List<ProximaRevisaoDTO> proximas = revisaoService.calcularProxRevisao();

        proximas = proximas.stream().filter( p -> placas.contains(p.getPlaca())).toList();


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 28, 28, 60, 36); // L,R,Top,Bottom
        PdfWriter writer = PdfWriter.getInstance(doc, baos);
        writer.setFullCompression();
        writer.setCompressionLevel(9);
        writer.setViewerPreferences(PdfWriter.PageLayoutOneColumn | PdfWriter.PageModeUseOutlines);

        doc.addTitle("Relatório - CarrosApp");
        doc.addAuthor("CarrosApp");
        doc.addSubject("Relatório da Dashboard");
        writer.setPageEvent(new BrandedPageEvent("CarrosApp", "Relatório da Dashboard", null));
        doc.open();




        addCover(doc, placas, months, mode);

// KPIs
        double totalGasto  = stats.getGasto().getSum().stream().map(MesValorDTO::getValor).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
        double totalLitros = stats.getLitros().getSum().stream().map(MesValorDTO::getValor).filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
        double mediaEf = stats.getEficiencia().getSum().stream().map(MesValorDTO::getValor).filter(Objects::nonNull).mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
        addKpis(doc, totalGasto, totalLitros, Double.isNaN(mediaEf) ? null : mediaEf);

// Charts (gere suas imagens com XChart CategoryChart)
        CategoryChart gastoChart  = gastoChart(stats);
        CategoryChart litrosChart = litrosChart(stats);
        CategoryChart eficChart   = eficChart(stats);

        com.lowagie.text.Image imgGasto  = toPdfImage(BitmapEncoder.getBufferedImage(gastoChart), 520);
        com.lowagie.text.Image imgLitros = toPdfImage(BitmapEncoder.getBufferedImage(litrosChart), 520);
        com.lowagie.text.Image imgEfic   = toPdfImage(BitmapEncoder.getBufferedImage(eficChart),   520);
        addCharts(doc, imgGasto, imgLitros, imgEfic);

// Próximas revisões

        addProximasTable(doc, proximas);

// finalize
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

    private CategoryChart gastoChart(CarStatsResponseDTO stats) {
        CategoryChart chart = buildCategoryChart("Gasto Mensal (R$)", "R$");
        addSeries(chart, stats, "gasto"); // usa sua função adaptada
        return chart;
    }

    private CategoryChart litrosChart(CarStatsResponseDTO stats) {
        CategoryChart chart = buildCategoryChart("Litros por Mês (L)", "Litros");
        addSeries(chart, stats, "litros");
        return chart;
    }

    private CategoryChart eficChart(CarStatsResponseDTO stats) {
        CategoryChart chart = buildCategoryChart("Eficiência (km/L)", "km/L");
        addSeries(chart, stats, "eficiencia");
        return chart;
    }

    private com.lowagie.text.Image toPdfImage(BufferedImage bi, float maxWidth) throws Exception {
        var img = com.lowagie.text.Image.getInstance(bi, null);
        if (img.getScaledWidth() > maxWidth) {
            float scale = maxWidth / img.getScaledWidth();
            img.scalePercent(scale * 100f);
        }
        return img;
    }


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
        Paragraph title = new Paragraph("Relatório da Dashboard", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        title.setSpacingBefore(6f);
        title.setSpacingAfter(6f);
        doc.add(title);

        PdfPTable meta = new PdfPTable(2);
        meta.setWidthPercentage(100);
        meta.setWidths(new float[]{1.1f, 3f});
        meta.getDefaultCell().setPadding(6f);

        meta.addCell(miniHeader("Carros"));
        meta.addCell(miniCell(String.join(", ", placas)));
        meta.addCell(miniHeader("Período"));
        meta.addCell(miniCell(months <= 0 ? "Todos os meses" : "Últimos " + months + " meses"));
        meta.addCell(miniHeader("Modo"));
        meta.addCell(miniCell("compare".equalsIgnoreCase(mode) ? "Comparar" : "Somar"));
        doc.add(meta);
        doc.add(new Paragraph(" "));
    }

    private PdfPCell miniHeader(String s){
        return getPdfPCell(s);
    }
    private PdfPCell miniCell(String s){
        PdfPCell c = new PdfPCell(new Phrase(s, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        c.setBorderColor(new Color(220,224,230)); c.setPadding(6f); return c;
    }

    private void addKpis(Document doc, double totalGasto, double totalLitros, Double mediaEf) throws Exception {
        PdfPTable kpis = new PdfPTable(3);
        kpis.setWidthPercentage(100);
        kpis.setSpacingBefore(4f);
        kpis.setWidths(new float[]{1,1,1});

        kpis.addCell(kpi("Gasto (período)", java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt","BR")).format(totalGasto)));
        kpis.addCell(kpi("Litros (período)", java.text.NumberFormat.getNumberInstance(new java.util.Locale("pt","BR")).format(totalLitros)));
        kpis.addCell(kpi("Eficiência média", (mediaEf==null||mediaEf.isNaN()) ? "s/dado" :
                java.text.NumberFormat.getNumberInstance(new java.util.Locale("pt","BR")).format(mediaEf) + " km/L"));

        doc.add(kpis);
        doc.add(new Paragraph(" "));
    }
    private PdfPCell kpi(String label, String value){
        PdfPCell box = new PdfPCell(); box.setPadding(10); box.setBorderColor(new Color(220,224,230));
        Paragraph p1 = new Paragraph(label, FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(110,110,110)));
        Paragraph p2 = new Paragraph(value, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        p1.setSpacingAfter(2f);
        box.addElement(p1); box.addElement(p2);
        return box;
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


    private void addCharts(Document doc, com.lowagie.text.Image... charts) throws Exception {
        PdfPTable grid = new PdfPTable(1); // mude para 2 se quiser 2 por linha
        grid.setWidthPercentage(100);
        for (var img : charts) {
            img.scaleToFit(520, 350); // ocupa largura útil mantendo altura agradável
            PdfPCell cell = new PdfPCell(img, true);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPaddingBottom(8f);
            grid.addCell(cell);
        }
        doc.add(grid);
    }

    private void addProximasTable(Document doc, List<ProximaRevisaoDTO> proximas) throws Exception {
        if (proximas == null || proximas.isEmpty()) return;

        // (Opcional) Ordene por mais urgente primeiro (maior % ou data mais próxima)
        List<ProximaRevisaoDTO> mutavel = new ArrayList<>(proximas);
        mutavel.sort(
                Comparator.comparing(
                        ProximaRevisaoDTO::getProximaRevisaoEstimada,
                        Comparator.nullsLast(Comparator.<Date>naturalOrder())
                ));

        doc.add(new Paragraph("Próximas revisões",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));

        // Placa | Última | Próxima | Progresso
        PdfPTable t = new PdfPTable(new float[]{1.0f, 1.1f, 1.1f, 2.2f});
        t.setWidthPercentage(100f);
        t.setSpacingBefore(6f);
        t.addCell(th("Placa"));
        t.addCell(th("Última"));
        t.addCell(th("Próxima"));
        t.addCell(th("Progresso"));

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt","BR"));

        boolean zebra = false;
        for (ProximaRevisaoDTO p : mutavel) {
            Color bg = zebra ? new Color(250, 250, 252) : Color.WHITE; zebra = !zebra;

            // Dados do DTO
            String placa = nvl(p.getPlaca(), "-");
            String ultima = p.getUltimaRevisao() == null ? "-" : df.format(p.getUltimaRevisao());
            String proxima = p.getProximaRevisaoEstimada() == null ? "-" : df.format(p.getProximaRevisaoEstimada());

            Double kmAlvo  = p.getKmRevisaoEstimado(); // alvo p/ próxima revisão
            Double kmAtual = p.getKmAtual();           // km atual de fato

            // Percentual (cap em 100). Se passou do alvo, marcamos como 100 mas a cor indica atraso.
            double perc = 0d;
            boolean atrasado = false;
            if (kmAlvo != null && kmAlvo > 0 && kmAtual != null && kmAtual >= 0) {
                perc = (kmAtual / kmAlvo) * 100d;
                atrasado = kmAtual >= kmAlvo;
                perc = Math.min(100d, perc);
            }

            // Label "65% (12.340 / 20.000 km)" ou "-"
            String label = (kmAtual != null && kmAlvo != null)
                    ? String.format("%s%% (%s / %s km)",
                    nf.format(Math.round(perc)),
                    nf.format(kmAtual),
                    nf.format(kmAlvo))
                    : "-";

            // Cor por status
            Color fill = new Color(37, 99, 235); // azul padrão
            if (atrasado) {
                fill = new Color(239, 68, 68); // vermelho
            } else if (perc >= 80d) {
                fill = new Color(245, 158, 11); // laranja
            } else {
                fill = new Color(16, 185, 129); // verde
            }

            // Células
            t.addCell(td(placa, bg, Element.ALIGN_LEFT));
            t.addCell(td(ultima, bg, Element.ALIGN_LEFT));
            t.addCell(td(proxima, bg, Element.ALIGN_LEFT));

            PdfPCell progressCell = new PdfPCell();
            progressCell.setBackgroundColor(bg);
            progressCell.setBorderColor(new Color(220,224,230));
            progressCell.setPadding(6f);
            progressCell.setMinimumHeight(18f);
            progressCell.setCellEvent(new ProgressBarCell((float) perc, label, fill));
            t.addCell(progressCell);
        }

        doc.add(t);

        // (Opcional) pequena legenda
         Paragraph legend = new Paragraph("Legenda: verde < 80% | laranja 80–99% | vermelho ≥ 100% (atrasado)",
                 FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(107,114,128)));
         legend.setSpacingBefore(2f);
         doc.add(legend);
    }

    private PdfPCell th(String s){
        return getPdfPCell(s);
    }

    private PdfPCell getPdfPCell(String s) {
        PdfPCell c = new PdfPCell(new Phrase(s, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9)));
        c.setBackgroundColor(new Color(245,247,250));
        c.setBorderColor(new Color(220,224,230));
        c.setPadding(6f);
        return c;
    }

    private PdfPCell td(String s, Color bg, int align){
        PdfPCell c = new PdfPCell(new Phrase(s, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        c.setBackgroundColor(bg);
        c.setBorderColor(new Color(220,224,230));
        c.setPadding(6f);
        c.setHorizontalAlignment(align);
        return c;
    }
    private static String nvl(String s, String def) { return (s == null || s.isEmpty()) ? def : s; }

}
