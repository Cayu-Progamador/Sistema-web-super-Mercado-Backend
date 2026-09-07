package com.backendSupermercado.supermercasdo.modules.ajustes_inventario.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.ajustes_inventario.dto.AjusteInventarioResponse;
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
public class ReporteAjusteInventarioPdfService {

    private static final Color VERDE = new DeviceRgb(27, 94, 32);
    private static final Color VERDE_CLARO = new DeviceRgb(232, 245, 233);
    private static final Color BORDE = new DeviceRgb(205, 215, 205);
    private static final Color GRIS = new DeviceRgb(100, 100, 100);
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(AjusteInventarioResponse ajuste) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        List<AjusteInventarioResponse.DetalleResponse> detalles =
                ajuste.detalles() == null ? List.of() : ajuste.detalles();

        try (PdfWriter writer = new PdfWriter(salida);
             PdfDocument pdf = new PdfDocument(writer);
             Document documento = new Document(pdf, PageSize.A4.rotate())) {

            documento.setMargins(25, 22, 28, 22);
            PdfFont normal = PdfFontFactory.createFont("Helvetica", "WinAnsi");
            PdfFont negrita = PdfFontFactory.createFont("Helvetica-Bold", "WinAnsi");

            documento.add(new Paragraph("SUPERMERCADO LA MARTITA")
                    .setFont(negrita).setFontSize(10).setFontColor(VERDE));
            documento.add(new Paragraph("COMPROBANTE DE AJUSTE DE INVENTARIO")
                    .setFont(negrita).setFontSize(18).setFontColor(VERDE)
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(-8).setMarginBottom(2));
            documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(FECHA_HORA))
                    .setFont(normal).setFontSize(8).setFontColor(GRIS)
                    .setTextAlignment(TextAlignment.RIGHT).setMarginTop(-16));

            Table resumen = new Table(UnitValue.createPercentArray(new float[] { 16, 17, 16, 17, 16, 18 }))
                    .useAllAvailableWidth().setMarginTop(4).setMarginBottom(8);
            agregarCampo(resumen, "Código", ajuste.codigo(), negrita, normal);
            agregarCampo(resumen, "Tipo", ajuste.tipo(), negrita, normal);
            agregarCampo(resumen, "Motivo", ajuste.motivo(), negrita, normal);
            agregarCampo(resumen, "Estado", ajuste.estado(), negrita, normal);
            agregarCampo(resumen, "Creado por", ajuste.creadoPor(), negrita, normal);
            agregarCampo(resumen, "Fecha", ajuste.fechaCreacion(), negrita, normal);
            documento.add(resumen);

            Table trazabilidad = new Table(UnitValue.createPercentArray(new float[] { 16, 17, 16, 17, 16, 18 }))
                    .useAllAvailableWidth().setMarginBottom(8);
            agregarCampo(trazabilidad, "Aprobado por", ajuste.aprobadoPor(), negrita, normal);
            agregarCampo(trazabilidad, "Fecha aprobación", ajuste.fechaAprobacion(), negrita, normal);
            agregarCampo(trazabilidad, "Aplicado por", ajuste.aplicadoPor(), negrita, normal);
            agregarCampo(trazabilidad, "Fecha aplicación", ajuste.fechaAplicacion(), negrita, normal);
            if ("SALIDA".equals(String.valueOf(ajuste.tipo()))) {
                agregarCampo(trazabilidad, "Responsable", ajuste.responsableLevantamiento(), negrita, normal);
            }
            agregarCampo(trazabilidad, "Lugar hallazgo", ajuste.lugarHallazgo(), negrita, normal);
            agregarCampo(trazabilidad, "Fecha hallazgo", ajuste.fechaHallazgo(), negrita, normal);
            agregarCampo(trazabilidad, "Encontrado por", ajuste.encontradoPor(), negrita, normal);
            documento.add(trazabilidad);

            if (ajuste.observacion() != null && !ajuste.observacion().isBlank()) {
                documento.add(new Paragraph("Justificación: " + ajuste.observacion())
                        .setFont(normal).setFontSize(8).setFontColor(GRIS).setMarginBottom(8));
            }

            String[] columnas = {
                    "Código", "Producto", "Almacén", "Ubicación", "Lote", "Vencimiento",
                    "Stock anterior", "Cantidad", "Stock resultante", "Unidad", "Costo unit.",
                    "Precio venta", "Ganancia / pérdida"
            };
            Table tabla = new Table(UnitValue.createPercentArray(columnas.length)).useAllAvailableWidth();
            for (String columna : columnas) {
                tabla.addHeaderCell(new Cell().setPadding(4).setBackgroundColor(VERDE_CLARO)
                        .setBorder(new SolidBorder(BORDE, .5f))
                        .add(new Paragraph(columna).setFont(negrita).setFontSize(6.5f)
                                .setFontColor(VERDE).setTextAlignment(TextAlignment.CENTER).setMargin(0)));
            }

            for (AjusteInventarioResponse.DetalleResponse detalle : detalles) {
                BigDecimal resultado = valor(detalle.valorTotal());
                BigDecimal gananciaPerdida = "ENTRADA".equals(String.valueOf(ajuste.tipo()))
                        ? valor(detalle.precioVenta()).subtract(valor(detalle.costoUnitario()))
                                .multiply(valor(detalle.cantidad()))
                        : resultado.negate();
                List<String> fila = List.of(
                        texto(detalle.codigoProducto()), texto(detalle.producto()), texto(detalle.almacen()),
                        texto(detalle.ubicacion()), texto(detalle.codigoLote()), texto(detalle.fechaVencimiento()),
                        texto(detalle.stockAnterior()), texto(detalle.cantidad()), texto(detalle.stockResultante()),
                        texto(detalle.unidad()), texto(detalle.costoUnitario()), texto(detalle.precioVenta()),
                        texto(gananciaPerdida));
                for (String valor : fila) {
                    tabla.addCell(new Cell().setPadding(3).setBorder(new SolidBorder(BORDE, .4f))
                            .add(new Paragraph(valor).setFont(normal).setFontSize(6.2f).setMargin(0)));
                }
            }

            if (detalles.isEmpty()) {
                tabla.addCell(new Cell(1, columnas.length).setPadding(12)
                        .setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(BORDE, .4f))
                        .add(new Paragraph("No existen productos en este ajuste")
                                .setFont(normal).setFontSize(9).setFontColor(GRIS)));
            }
            documento.add(tabla);

            documento.add(new Paragraph("Cantidad total: " + texto(ajuste.cantidadTotal())
                    + "    |    Valor total: Bs " + texto(ajuste.valorTotal()))
                    .setFont(negrita).setFontSize(9).setFontColor(VERDE)
                    .setTextAlignment(TextAlignment.RIGHT).setMarginTop(8));
            documento.add(new Paragraph("Documento generado por el sistema de inventario")
                    .setFont(normal).setFontSize(7).setFontColor(GRIS)
                    .setTextAlignment(TextAlignment.CENTER).setMarginTop(14));
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar el comprobante PDF del ajuste", exception);
        }

        return salida.toByteArray();
    }

    private void agregarCampo(Table tabla, String etiqueta, Object valor, PdfFont negrita, PdfFont normal) {
        tabla.addCell(new Cell().setPadding(3).setBackgroundColor(VERDE_CLARO)
                .setBorder(new SolidBorder(BORDE, .4f))
                .add(new Paragraph(etiqueta).setFont(negrita).setFontSize(7).setFontColor(VERDE).setMargin(0)));
        tabla.addCell(new Cell().setPadding(3).setBorder(new SolidBorder(BORDE, .4f))
                .add(new Paragraph(texto(valor)).setFont(normal).setFontSize(7).setMargin(0)));
    }

    private static BigDecimal valor(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private static String texto(Object valor) {
        if (valor == null || valor.toString().isBlank()) return "-";
        if (valor instanceof LocalDateTime fecha) return fecha.format(FECHA_HORA);
        if (valor instanceof LocalDate fecha) return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (valor instanceof BigDecimal numero) {
            return numero.setScale(2, RoundingMode.HALF_UP).toPlainString();
        }
        return valor.toString();
    }
}
