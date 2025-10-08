package com.revisao.demo.utils.pdf;

import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.Font;
import java.awt.*;

public class ProgressBarCell implements PdfPCellEvent {
    private final float percent; // 0..100
    private final String label;
    private final Color fillColor;

    public ProgressBarCell(float percent, String label, Color fillColor) {
        this.percent = Math.max(0, Math.min(100, percent));
        this.label = label;
        this.fillColor = fillColor != null ? fillColor : new Color(66, 133, 244);
    }

    @Override
    public void cellLayout(PdfPCell cell, Rectangle rect, PdfContentByte[] canvases) {
        PdfContentByte bg = canvases[PdfPTable.BACKGROUNDCANVAS];
        PdfContentByte text = canvases[PdfPTable.TEXTCANVAS];

        final float pad = 2.5f;
        float x = rect.getLeft() + pad;
        float y = rect.getBottom() + pad;
        float w = rect.getWidth() - pad * 2;
        float h = rect.getHeight() - pad * 2;

        // trilho
        bg.saveState();
        bg.setColorFill(new Color(230, 232, 236));
        bg.roundRectangle(x, y, w, h, 3f);
        bg.fill();

        // preenchido
        float filled = w * (percent / 100f);
        if (filled > 0.5f) {
            bg.setColorFill(fillColor);
            bg.roundRectangle(x, y, filled, h, 3f);
            bg.fill();
        }
        bg.restoreState();

        // texto centralizado (troca cor p/ contraste se barra pequena)
        if (label != null && !label.isEmpty()) {
            boolean onLight = filled < w * 0.35f;
            Color txtColor = onLight ? new Color(55, 65, 81) : Color.WHITE;
            Font f = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, txtColor);
            ColumnText.showTextAligned(
                    text, Element.ALIGN_CENTER, new Phrase(label, f),
                    rect.getLeft() + rect.getWidth() / 2,
                    rect.getBottom() + rect.getHeight() / 2 - 2f, 0
            );
        }
    }
}
