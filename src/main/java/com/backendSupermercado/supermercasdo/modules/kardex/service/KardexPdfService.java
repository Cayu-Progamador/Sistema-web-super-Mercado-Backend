package com.backendSupermercado.supermercasdo.modules.kardex.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.kardex.dto.KardexFiltroDto;
import com.backendSupermercado.supermercasdo.modules.kardex.dto.KardexResponseDto;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
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
public class KardexPdfService {

    private static final Color VERDE = new DeviceRgb(27, 94, 32);
    private static final Color VERDE_CLARO = new DeviceRgb(232, 245, 233);
    private static final Color BORDE = new DeviceRgb(205, 215, 205);
    private static final Color GRIS = new DeviceRgb(100, 100, 100);
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(List<KardexResponseDto> movimientos, KardexFiltroDto filtros) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(salida);
             PdfDocument pdf = new PdfDocument(writer);
             Document documento = new Document(pdf, PageSize.A4.rotate())) {

            documento.setMargins(25, 22, 28, 22);
            PdfFont normal = PdfFontFactory.createFont("Helvetica", "WinAnsi");
            PdfFont negrita = PdfFontFactory.createFont("Helvetica-Bold", "WinAnsi");

            agregarEncabezado(documento, filtros, normal, negrita);
            agregarTabla(documento, movimientos, normal, negrita);
            agregarTotales(documento, movimientos, normal, negrita);
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar el PDF del Kardex", exception);
        }

        return salida.toByteArray();
    }

    private void agregarEncabezado(
            Document documento,
            KardexFiltroDto filtros,
            PdfFont normal,
            PdfFont negrita) {
        documento.add(new Paragraph("SUPERMERCADO LA MARTITA")
                .setFont(negrita)
                .setFontSize(10)
                .setFontColor(VERDE)
                .setMarginBottom(2));
        documento.add(new Paragraph("REPORTE DE KARDEX")
                .setFont(negrita)
                .setFontSize(18)
                .setFontColor(VERDE)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(0)
                .setMarginBottom(3));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(FECHA_HORA))
                .setFont(normal)
                .setFontSize(8)
                .setFontColor(GRIS)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(-16)
                .setMarginBottom(8));
        documento.add(new Paragraph(describirFiltros(filtros))
                .setFont(normal)
                .setFontSize(8)
                .setFontColor(GRIS)
                .setMarginBottom(10));
    }

    private void agregarTabla(
            Document documento,
            List<KardexResponseDto> movimientos,
            PdfFont normal,
            PdfFont negrita) {
        float[] anchos = {10, 8, 16, 11, 12, 9, 7, 7, 7, 6, 9, 12};
        Table tabla = new Table(UnitValue.createPercentArray(anchos)).useAllAvailableWidth();

        String[] titulos = {
                "Fecha/Hora", "Código", "Producto", "Documento", "Operación", "Lote",
                "Entrada", "Salida", "Saldo", "Unidad", "Usuario", "Observación"
        };
        for (String titulo : titulos) {
            tabla.addHeaderCell(celdaEncabezado(titulo, negrita));
        }

        for (KardexResponseDto movimiento : movimientos) {
            tabla.addCell(celda(formatearFecha(movimiento.fecha()), normal, TextAlignment.CENTER));
            tabla.addCell(celda(valor(movimiento.codigoProducto()), normal, TextAlignment.LEFT));
            tabla.addCell(celda(valor(movimiento.producto()), normal, TextAlignment.LEFT));
            tabla.addCell(celda(valor(movimiento.documento()), normal, TextAlignment.LEFT));
            tabla.addCell(celda(valor(movimiento.operacion()), normal, TextAlignment.LEFT));
            tabla.addCell(celda(valor(movimiento.lote()), normal, TextAlignment.CENTER));
            tabla.addCell(celda(numero(movimiento.entrada()), normal, TextAlignment.RIGHT));
            tabla.addCell(celdaSalida(movimiento.salida(), normal));
            tabla.addCell(celda(numero(movimiento.saldo()), normal, TextAlignment.RIGHT));
            tabla.addCell(celda(valor(movimiento.unidad()), normal, TextAlignment.CENTER));
            tabla.addCell(celda(valor(movimiento.usuario()), normal, TextAlignment.LEFT));
            tabla.addCell(celda(valor(movimiento.observacion()), normal, TextAlignment.LEFT));
        }

        if (movimientos.isEmpty()) {
            tabla.addCell(new Cell(1, titulos.length)
                    .setPadding(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(BORDE, .4f))
                    .add(new Paragraph("No existen movimientos para los filtros seleccionados")
                            .setFont(normal)
                            .setFontSize(9)
                            .setFontColor(GRIS)));
        }

        documento.add(tabla);
    }

    private void agregarTotales(
            Document documento,
            List<KardexResponseDto> movimientos,
            PdfFont normal,
            PdfFont negrita) {
        BigDecimal entradas = movimientos.stream()
                .map(KardexResponseDto::entrada)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal salidas = movimientos.stream()
                .map(KardexResponseDto::salida)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        documento.add(new Paragraph(
                "Movimientos: " + movimientos.size()
                        + "   |   Entradas: " + numero(entradas)
                        + "   |   Salidas: " + numero(salidas))
                .setFont(negrita)
                .setFontSize(9)
                .setFontColor(VERDE)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(8));
        documento.add(new Paragraph(
                "Los saldos corresponden al producto, lote y ubicación de cada movimiento.")
                .setFont(normal)
                .setFontSize(7)
                .setFontColor(GRIS)
                .setTextAlignment(TextAlignment.RIGHT));
    }

    private Cell celdaEncabezado(String texto, PdfFont fuente) {
        return new Cell()
                .setPadding(4)
                .setBackgroundColor(VERDE_CLARO)
                .setBorder(new SolidBorder(BORDE, .5f))
                .add(new Paragraph(texto)
                        .setFont(fuente)
                        .setFontSize(7)
                        .setFontColor(VERDE)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMargin(0));
    }

    private Cell celda(String texto, PdfFont fuente, TextAlignment alineacion) {
        return new Cell()
                .setPadding(3)
                .setBorder(new SolidBorder(BORDE, .4f))
                .add(new Paragraph(texto)
                        .setFont(fuente)
                        .setFontSize(6.5f)
                        .setTextAlignment(alineacion)
                        .setMargin(0));
    }

    private Cell celdaSalida(BigDecimal salida, PdfFont fuente) {
        Cell celda = celda(numero(salida), fuente, TextAlignment.RIGHT);
        if (salida != null) {
            celda.setFontColor(ColorConstants.RED);
        }
        return celda;
    }

    private String describirFiltros(KardexFiltroDto filtros) {
        StringBuilder descripcion = new StringBuilder("Filtros: ");
        boolean tieneFiltros = false;

        if (filtros.buscar() != null && !filtros.buscar().isBlank()) {
            descripcion.append("búsqueda '").append(filtros.buscar().trim()).append("'");
            tieneFiltros = true;
        }
        if (filtros.idProducto() != null) {
            descripcion.append(tieneFiltros ? " · " : "").append("producto seleccionado");
            tieneFiltros = true;
        }
        if (filtros.idLote() != null) {
            descripcion.append(tieneFiltros ? " · " : "").append("lote seleccionado");
            tieneFiltros = true;
        }
        if (filtros.fechaDesde() != null) {
            descripcion.append(tieneFiltros ? " · " : "")
                    .append("desde ").append(filtros.fechaDesde());
            tieneFiltros = true;
        }
        if (filtros.fechaHasta() != null) {
            descripcion.append(tieneFiltros ? " · " : "")
                    .append("hasta ").append(filtros.fechaHasta());
            tieneFiltros = true;
        }

        return tieneFiltros ? descripcion.toString() : "Filtros: todos los movimientos";
    }

    private String formatearFecha(LocalDateTime fecha) {
        return fecha == null ? "-" : fecha.format(FECHA_HORA);
    }

    private String valor(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }

    private String numero(BigDecimal valor) {
        return valor == null ? "-" : valor.stripTrailingZeros().toPlainString();
    }
}
