package com.backendSupermercado.supermercasdo.modules.reportes.venta.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaExportacion;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class ReporteVentaPdfService {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(ReporteVentaExportacion reporte) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(salida);
             PdfDocument pdf = new PdfDocument(writer);
             Document documento = new Document(pdf, PageSize.A4.rotate())) {

            documento.setMargins(28, 22, 30, 22);
            PdfFont normal = PdfFontFactory.createFont("Helvetica", "WinAnsi");
            PdfFont negrita = PdfFontFactory.createFont("Helvetica-Bold", "WinAnsi");

            documento.add(new Paragraph("SUPERMERCADO LA MARTITA")
                    .setFont(negrita).setFontSize(10).setFontColor(ColorConstants.BLACK)
                    .setTextAlignment(TextAlignment.LEFT).setMargin(0));
            documento.add(new Paragraph(reporte.titulo().toUpperCase())
                    .setFont(negrita).setFontSize(17).setFontColor(ColorConstants.BLACK)
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(-8).setMarginBottom(2));
            documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(FECHA_HORA))
                    .setFont(normal).setFontSize(8).setFontColor(ColorConstants.BLACK)
                    .setTextAlignment(TextAlignment.RIGHT).setMarginTop(-15).setMarginBottom(4));
            documento.add(new Paragraph(reporte.filtros())
                    .setFont(normal).setFontSize(8).setFontColor(ColorConstants.BLACK).setMarginBottom(10));

            Table tabla = new Table(UnitValue.createPercentArray(reporte.columnas().size()))
                    .useAllAvailableWidth();
            for (String columna : reporte.columnas()) {
                tabla.addHeaderCell(celda(columna, negrita, 7.2f, TextAlignment.CENTER));
            }
            for (List<String> fila : reporte.filas()) {
                for (String valor : fila) {
                    tabla.addCell(celda(valor, normal, 6.7f, TextAlignment.LEFT));
                }
            }
            if (reporte.filas().isEmpty()) {
                tabla.addCell(new Cell(1, reporte.columnas().size())
                        .setBorder(new SolidBorder(ColorConstants.BLACK, .5f)).setPadding(12)
                        .setTextAlignment(TextAlignment.CENTER)
                        .add(new Paragraph("No existen registros para los filtros seleccionados")
                                .setFont(normal).setFontSize(9).setMargin(0)));
            }
            documento.add(tabla);
            documento.add(new Paragraph("Total de registros: " + reporte.filas().size())
                    .setFont(negrita).setFontSize(9).setFontColor(ColorConstants.BLACK)
                    .setTextAlignment(TextAlignment.RIGHT).setMarginTop(8));
            documento.add(new Paragraph("Documento generado automáticamente por el sistema.")
                    .setFont(normal).setFontSize(7).setFontColor(ColorConstants.BLACK)
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(10));
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar el reporte de ventas en PDF", exception);
        }
        return salida.toByteArray();
    }

    private Cell celda(String texto, PdfFont fuente, float tamano, TextAlignment alineacion) {
        return new Cell().setPadding(4).setBorder(new SolidBorder(ColorConstants.BLACK, .5f))
                .setBackgroundColor(ColorConstants.WHITE)
                .add(new Paragraph(texto == null ? "-" : texto).setFont(fuente).setFontSize(tamano)
                        .setFontColor(ColorConstants.BLACK).setTextAlignment(alineacion).setMargin(0));
    }
}
