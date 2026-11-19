package com.backendSupermercado.supermercasdo.modules.caja.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.caja.dto.SesionCajaResponse;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
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
public class CajaArqueoPdfService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(SesionCajaResponse sesion) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(salida);
             PdfDocument pdf = new PdfDocument(writer);
             Document documento = new Document(pdf)) {

            documento.setMargins(32, 36, 32, 36);
            PdfFont normal = PdfFontFactory.createFont("Helvetica", "WinAnsi");
            PdfFont negrita = PdfFontFactory.createFont("Helvetica-Bold", "WinAnsi");

            documento.add(new Paragraph("ARQUEO DE CAJA")
                    .setFont(negrita).setFontSize(16).setTextAlignment(TextAlignment.CENTER).setMarginBottom(3));
            documento.add(new Paragraph("Comprobante de cierre de sesión")
                    .setFont(normal).setFontSize(9).setTextAlignment(TextAlignment.CENTER).setMarginBottom(18));

            Table datos = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }))
                    .useAllAvailableWidth().setBorder(new SolidBorder(ColorConstants.BLACK, .7f));
            dato(datos, "Caja", texto(sesion.caja()), normal, negrita);
            dato(datos, "Cajero responsable", texto(sesion.cajero()), normal, negrita);
            dato(datos, "Fecha de apertura", fecha(sesion.fechaApertura()), normal, negrita);
            dato(datos, "Fecha de cierre", fecha(sesion.fechaCierre()), normal, negrita);
            dato(datos, "Estado", texto(sesion.estado()), normal, negrita);
            documento.add(datos.setMarginBottom(18));

            documento.add(titulo("MOVIMIENTOS DE LA SESIÓN", negrita));
            Table movimientos = tabla(normal, negrita);
            fila(movimientos, "Fondo inicial", sesion.montoInicial(), normal, negrita, false);
            fila(movimientos, "Ventas en efectivo", sesion.efectivo(), normal, negrita, false);
            fila(movimientos, "Ventas por QR", sesion.qr(), normal, negrita, false);
            fila(movimientos, "Ventas mixtas", sesion.mixto(), normal, negrita, false);
            fila(movimientos, "  - Efectivo de ventas mixtas", sesion.mixtoEfectivo(), normal, negrita, false);
            fila(movimientos, "  - QR de ventas mixtas", sesion.mixtoQr(), normal, negrita, false);
            fila(movimientos, "Ventas a crédito", sesion.credito(), normal, negrita, false);
            fila(movimientos, "Pagos de crédito recibidos", sesion.pagosCredito(), normal, negrita, false);
            fila(movimientos, "  - Efectivo de pagos de crédito", sesion.creditoEfectivo(), normal, negrita, false);
            fila(movimientos, "  - QR de pagos de crédito", sesion.creditoQr(), normal, negrita, false);
            fila(movimientos, "Crédito pendiente", sesion.creditoPendiente(), normal, negrita, false);
            documento.add(movimientos.setMarginBottom(18));

            documento.add(titulo("RESULTADO DEL ARQUEO", negrita));
            Table resultado = tabla(normal, negrita);
            fila(resultado, "Efectivo esperado en caja", sesion.montoEsperado(), normal, negrita, true);
            fila(resultado, "Efectivo contado", sesion.efectivoContado(), normal, negrita, true);
            fila(resultado, etiquetaDiferencia(sesion.diferencia()), sesion.diferencia(), normal, negrita, true);
            documento.add(resultado.setMarginBottom(10));

            documento.add(new Paragraph("Observación: " + texto(sesion.observacion(), "Sin observaciones"))
                    .setFont(normal).setFontSize(9).setMarginBottom(10));
            documento.add(new Paragraph("____________________________________________")
                    .setFont(normal).setTextAlignment(TextAlignment.CENTER).setMargin(0));
            documento.add(new Paragraph("Firma del cajero responsable")
                    .setFont(normal).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMargin(0));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el PDF de arqueo", ex);
        }

        return salida.toByteArray();
    }

    private Table tabla(PdfFont normal, PdfFont negrita) {
        Table tabla = new Table(UnitValue.createPercentArray(new float[] { 3, 1 })).useAllAvailableWidth();
        tabla.addHeaderCell(celda("Concepto", negrita, TextAlignment.LEFT, true));
        tabla.addHeaderCell(celda("Importe (Bs)", negrita, TextAlignment.RIGHT, true));
        return tabla;
    }

    private void fila(Table tabla, String concepto, BigDecimal importe, PdfFont normal, PdfFont negrita, boolean destacado) {
        tabla.addCell(celda(concepto, destacado ? negrita : normal, TextAlignment.LEFT, false));
        tabla.addCell(celda(dinero(importe), destacado ? negrita : normal, TextAlignment.RIGHT, false));
    }

    private void dato(Table tabla, String etiqueta, String valor, PdfFont normal, PdfFont negrita) {
        tabla.addCell(celda(etiqueta, normal, TextAlignment.LEFT, false));
        tabla.addCell(celda(valor, negrita, TextAlignment.RIGHT, false));
    }

    private Cell celda(String valor, PdfFont fuente, TextAlignment alineacion, boolean encabezado) {
        Cell celda = new Cell().setPadding(6).setBorder(new SolidBorder(ColorConstants.BLACK, .4f));
        if (encabezado) celda.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        return celda.add(new Paragraph(valor).setFont(fuente).setFontSize(9).setTextAlignment(alineacion).setMargin(0));
    }

    private Paragraph titulo(String texto, PdfFont fuente) {
        return new Paragraph(texto).setFont(fuente).setFontSize(10).setMarginBottom(6);
    }

    private String etiquetaDiferencia(BigDecimal diferencia) {
        if (diferencia == null || diferencia.compareTo(BigDecimal.ZERO) == 0) return "Diferencia (sin diferencia)";
        return diferencia.signum() > 0 ? "Diferencia (sobrante)" : "Diferencia (faltante)";
    }

    private String fecha(java.time.LocalDateTime valor) { return valor == null ? "--" : valor.format(FECHA); }
    private String dinero(BigDecimal valor) { return valor == null ? "0.00" : valor.setScale(2).toPlainString(); }
    private String texto(Object valor) { return texto(valor, "--"); }
    private String texto(Object valor, String defecto) { return valor == null || valor.toString().isBlank() ? defecto : valor.toString(); }
}
