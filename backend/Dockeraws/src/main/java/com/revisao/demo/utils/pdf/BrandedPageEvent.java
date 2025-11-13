package com.revisao.demo.utils.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;

import java.awt.*;
import java.awt.Image;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BrandedPageEvent extends PdfPageEventHelper {

    private String appName;
    private String subtitle;
    private final Image logo; // pode ser null
    private PdfTemplate total;
    private final Font headerTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
    private final Font headerSub = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.WHITE);
    private final Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(90, 98, 109));
    private final Color brand = new Color(15, 23, 42); // slate-900
    private final Color brandLine = new Color(203, 213, 225); // slate-300

    public BrandedPageEvent(String appName, String subtitle, Image logoOpt) {
        this.appName = appName;
        this.subtitle = subtitle;
        this.logo = logoOpt;
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(30, 16);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        com.lowagie.text.Rectangle page = document.getPageSize();

        // HEADER: faixa sólida
        float headerHeight = 34f; // altura da faixa
        float h = page.getTop();

        cb.saveState();
        cb.setColorFill(brand);
        cb.rectangle(page.getLeft(), h - headerHeight, page.getWidth(), headerHeight);
        cb.fill();
        cb.restoreState();

        float leftX  = document.leftMargin();
        float rightX = page.getRight() - document.rightMargin();

// cálculo automático para centralizar textos dentro da faixa
        float paddingTop = 8f; // margem interna superior da faixa
        float textStartY = h - headerHeight + paddingTop;

        /*if (logo != null) {
            float targetH = 18f;
            float scale = targetH / logo.getScaledHeight();
            logo.scalePercent(scale * 100f);
            logo.setAbsolutePosition(page.getLeft() + 10, h - 30);
            try { writer.getDirectContent().addImage(logo); } catch (Exception ignore) {}
            leftX += 26; // dá espaço pro logo
        }*/

// desenha o título principal ("CarrosApp")
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                new Phrase(appName, headerTitle),
                page.getLeft() + leftX,
                textStartY + 10f, 0);

// desenha o subtítulo ("Relatório da Dashboard")
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                new Phrase(subtitle, headerSub),
                page.getLeft() + leftX,
                textStartY - 2f, 0);

        // FOOTER: linha + página X de Y + data/hora
        cb.saveState();
        cb.setColorStroke(brandLine);
        cb.moveTo(page.getLeft(), document.bottom() - 8);
        cb.lineTo(page.getRight(), document.bottom() - 8);
        cb.stroke();
        cb.restoreState();

        String leftFooter  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(LocalDateTime.now());
        String pageText    = "pág. " + writer.getPageNumber() + " de ";
        float textBase     = document.bottom() - 22;

        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(leftFooter, footerFont),
                document.left(), textBase, 0);

        float pageTextWidth = footerFont.getCalculatedBaseFont(false).getWidthPoint(pageText, 9);
        float pageTextX = rightX - 30;
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(pageText, footerFont),
                pageTextX, textBase, 0);
        cb.addTemplate(total, pageTextX + 2, textBase); // “Y” será preenchido em onCloseDocument
    }

    @Override public void onCloseDocument(PdfWriter writer, Document document) {
        ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                new Phrase(String.valueOf(writer.getPageNumber() - 1), footerFont), 2, 0, 0);
    }

}
