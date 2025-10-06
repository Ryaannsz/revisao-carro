package com.revisao.demo.utils.pdf;

import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Font;
import java.awt.*;

public class PdfUtils {

    public static final Color SLATE_BG = new Color(245,247,250);
    public static final Color SLATE_BORDER = new Color(220,224,230);
    public static final Color TITLE = new Color(20,20,20);
    // fontes
    public static final Font H1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, TITLE);
    public static final Font H2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, TITLE);
    public static final Font TXT = FontFactory.getFont(FontFactory.HELVETICA, 11);
    public static final Font SMALL_MUTED = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(110,110,110));
    public static final java.text.NumberFormat CURRENCY = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt","BR"));
    public static final java.text.NumberFormat NUM = java.text.NumberFormat.getNumberInstance(new java.util.Locale("pt","BR"));

    public static Phrase kpiTitle(String s){ return new Phrase(s, SMALL_MUTED); }
    public static Phrase kpiValue(String s){ return new Phrase(s, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)); }

    public static PdfPCell cell(String text, Font f, int align){
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setHorizontalAlignment(align);
        c.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c.setPadding(8f);
        c.setBorderColor(SLATE_BORDER);
        return c;
    }

    public static PdfPCell headerCell(String text){
        PdfPCell c = cell(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10), Element.ALIGN_LEFT);
        c.setBackgroundColor(SLATE_BG);
        return c;
    }
}
