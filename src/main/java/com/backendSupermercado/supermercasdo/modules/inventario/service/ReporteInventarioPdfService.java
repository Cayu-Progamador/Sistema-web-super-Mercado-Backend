package com.backendSupermercado.supermercasdo.modules.inventario.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.inventario.dto.ExistenciaResponseDto;
import com.backendSupermercado.supermercasdo.modules.lotes.dto.LoteResponseDto;
import com.backendSupermercado.supermercasdo.modules.vencimientos.dto.VencimientoResponseDto;
import com.itextpdf.kernel.colors.Color;
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
public class ReporteInventarioPdfService {

    private static final Color VERDE = new DeviceRgb(27, 94, 32);
    private static final Color VERDE_CLARO = new DeviceRgb(232, 245, 233);
    private static final Color BORDE = new DeviceRgb(205, 215, 205);
    private static final Color GRIS = new DeviceRgb(100, 100, 100);
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generarExistencias(List<ExistenciaResponseDto> datos, String filtros) {
        String[] columnas = {
                "Código", "Producto", "Almacén", "Ubicación", "Lote", "Vencimiento",
                "Stock", "Mínimo", "Máximo", "Unidad", "Estado"
        };
        List<List<String>> filas = datos.stream().map(item -> List.of(
                texto(item.codigoProducto()),
                texto(item.nombreProducto()),
                texto(item.almacen()),
                texto(item.nombreUbicacion() != null ? item.nombreUbicacion() : item.codigoUbicacion()),
                texto(item.codigoLote()),
                texto(item.fechaVencimiento()),
                texto(item.cantidad()),
                texto(item.stockMinimo()),
                texto(item.stockMaximo()),
                texto(item.unidadBase()),
                texto(item.estado()))).toList();
        return generar("REPORTE DE EXISTENCIAS", columnas, filas, filtros);
    }

    public byte[] generarLotes(List<LoteResponseDto> datos, String filtros) {
        String[] columnas = {
                "Código de lote", "Producto", "Ingreso", "Fabricación", "Vencimiento",
                "Cantidad", "Estado", "Almacén", "Ubicación"
        };
        List<List<String>> filas = datos.stream().map(item -> List.of(
                texto(item.codigoLote()),
                texto(item.producto()),
                texto(item.fechaIngreso()),
                texto(item.fechaFabricacion()),
                texto(item.fechaVencimiento()),
                texto(item.cantidad()),
                texto(item.estado()),
                texto(item.almacen()),
                texto(item.ubicacion()))).toList();
        return generar("REPORTE DE LOTES", columnas, filas, filtros);
    }

    public byte[] generarVencimientos(List<VencimientoResponseDto> datos, String filtros) {
        String[] columnas = {
                "Código", "Producto", "Lote", "Vencimiento", "Días", "Cantidad",
                "Unidad", "Almacén", "Ubicación", "Estado"
        };
        List<List<String>> filas = datos.stream().map(item -> List.of(
                texto(item.codigoProducto()),
                texto(item.producto()),
                texto(item.lote()),
                texto(item.fechaVencimiento()),
                String.valueOf(item.diasRestantes()),
                texto(item.cantidad()),
                texto(item.unidad()),
                texto(item.almacen()),
                texto(item.ubicacion()),
                texto(item.estado()))).toList();
        return generar("REPORTE DE VENCIMIENTOS", columnas, filas, filtros);
    }

    private byte[] generar(
            String titulo,
            String[] columnas,
            List<List<String>> filas,
            String filtros) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(salida);
             PdfDocument pdf = new PdfDocument(writer);
             Document documento = new Document(pdf, PageSize.A4.rotate())) {

            documento.setMargins(25, 22, 28, 22);
            PdfFont normal = PdfFontFactory.createFont("Helvetica", "WinAnsi");
            PdfFont negrita = PdfFontFactory.createFont("Helvetica-Bold", "WinAnsi");

            documento.add(new Paragraph("SUPERMERCADO LA MARTITA")
                    .setFont(negrita).setFontSize(10).setFontColor(VERDE));
            documento.add(new Paragraph(titulo)
                    .setFont(negrita).setFontSize(18).setFontColor(VERDE)
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(-8).setMarginBottom(2));
            documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(FECHA_HORA))
                    .setFont(normal).setFontSize(8).setFontColor(GRIS)
                    .setTextAlignment(TextAlignment.RIGHT).setMarginTop(-16));
            documento.add(new Paragraph(filtros)
                    .setFont(normal).setFontSize(8).setFontColor(GRIS).setMarginBottom(9));

            Table tabla = new Table(UnitValue.createPercentArray(columnas.length)).useAllAvailableWidth();
            for (String columna : columnas) {
                tabla.addHeaderCell(new Cell().setPadding(4).setBackgroundColor(VERDE_CLARO)
                        .setBorder(new SolidBorder(BORDE, .5f))
                        .add(new Paragraph(columna).setFont(negrita).setFontSize(7)
                                .setFontColor(VERDE).setTextAlignment(TextAlignment.CENTER).setMargin(0)));
            }

            for (List<String> fila : filas) {
                for (String valor : fila) {
                    tabla.addCell(new Cell().setPadding(3).setBorder(new SolidBorder(BORDE, .4f))
                            .add(new Paragraph(valor).setFont(normal).setFontSize(6.5f).setMargin(0)));
                }
            }

            if (filas.isEmpty()) {
                tabla.addCell(new Cell(1, columnas.length).setPadding(12)
                        .setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(BORDE, .4f))
                        .add(new Paragraph("No existen registros para los filtros seleccionados")
                                .setFont(normal).setFontSize(9).setFontColor(GRIS)));
            }

            documento.add(tabla);
            documento.add(new Paragraph("Total de registros: " + filas.size())
                    .setFont(negrita).setFontSize(9).setFontColor(VERDE)
                    .setTextAlignment(TextAlignment.RIGHT).setMarginTop(8));
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar el reporte PDF", exception);
        }

        return salida.toByteArray();
    }

    private static String texto(Object valor) {
        if (valor == null || valor.toString().isBlank()) return "-";
        if (valor instanceof java.math.BigDecimal numero) {
            return numero.stripTrailingZeros().toPlainString();
        }
        return valor.toString();
    }
}
