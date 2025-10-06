package com.revisao.demo.utils.pdf;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;

public class PdfHeaderFooter extends PdfPageEventHelper {

    private final String headerText;
    private final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(100,100,100));
    private final Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(120,120,120));

    public PdfHeaderFooter(String headerText) { this.headerText = headerText; }

    @Override public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        com.lowagie.text.Rectangle page = document.getPageSize();

        // Header (esquerda)
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                new Phrase(headerText, headerFont),
                document.left(), page.getTop() - 20, 0);

        // Footer: data + número da página (direita)
        String right = "Gerado em " + java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(java.time.LocalDateTime.now())
                + " · pág. " + writer.getPageNumber();
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                new Phrase(right, footerFont),
                page.getRight() - document.rightMargin(), page.getBottom() + 20, 0);
    }
}
