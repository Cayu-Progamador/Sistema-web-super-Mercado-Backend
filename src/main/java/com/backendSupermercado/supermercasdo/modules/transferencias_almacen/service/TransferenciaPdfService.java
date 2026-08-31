package com.backendSupermercado.supermercasdo.modules.transferencias_almacen.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.entity.TransferenciaAlmacen;
import com.backendSupermercado.supermercasdo.modules.transferencias_almacen.entity.DetalleTransferencia;
import com.itextpdf.kernel.colors.DeviceRgb;
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
import org.springframework.stereotype.Service;

@Service
public class TransferenciaPdfService {
    private static final DeviceRgb VERDE = new DeviceRgb(27, 94, 32);
    private static final DeviceRgb VERDE_MEDIO = new DeviceRgb(76, 125, 50);
    private static final DeviceRgb VERDE_CLARO = new DeviceRgb(232, 245, 233);
    private static final DeviceRgb GRIS_BORDE = new DeviceRgb(205, 215, 202);
    private static final DeviceRgb GRIS_TEXTO = new DeviceRgb(85, 95, 85);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(TransferenciaAlmacen t) {
        try {
            var out = new ByteArrayOutputStream();
            try (var pdf = new PdfDocument(new PdfWriter(out)); var doc = new Document(pdf, PageSize.A4)) {
                doc.setMargins(30, 32, 30, 32);
                doc.add(new Paragraph("SUPERMERCADO").setFontSize(11).setBold().setFontColor(VERDE));
                doc.add(new Paragraph("DETALLE DE TRANSFERENCIA").setFontSize(20).setBold().setFontColor(VERDE).setTextAlignment(TextAlignment.CENTER).setMarginTop(3));
                doc.add(new Paragraph(t.getCodigo()).setFontSize(11).setBold().setFontColor(VERDE_MEDIO).setTextAlignment(TextAlignment.CENTER).setMarginBottom(16));

                Table ficha = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
                ficha.addCell(celda("Estado", texto(t.getEstadoTransferencia() == null ? null : t.getEstadoTransferencia().getNombre()), true));
                ficha.addCell(celda("Fecha", t.getFechaTransferencia() == null ? "-" : t.getFechaTransferencia().format(FECHA), true));
                ficha.addCell(celda("Responsable", t.getUsuario() == null ? "-" : t.getUsuario().getUsername(), false));
                ficha.addCell(celda("Observación", texto(t.getObservacion()), false));
                doc.add(ficha.setMarginBottom(14));

                Table ruta = new Table(UnitValue.createPercentArray(new float[]{1, 0.18f, 1})).useAllAvailableWidth();
                ruta.addCell(celda("ORIGEN", ubicacion(t.getUbicacionOrigen()), true));
                ruta.addCell(new Cell().setBorder(null).add(new Paragraph("→").setFontSize(22).setBold().setFontColor(VERDE).setTextAlignment(TextAlignment.CENTER)));
                ruta.addCell(celda("DESTINO", ubicacion(t.getUbicacionDestino()), true));
                doc.add(ruta.setMarginBottom(18));

                doc.add(new Paragraph("PRODUCTOS TRANSFERIDOS").setFontSize(12).setBold().setFontColor(VERDE).setMarginBottom(7));
                Table tabla = new Table(UnitValue.createPercentArray(new float[]{5, 35, 25, 18})).useAllAvailableWidth();
                for (String encabezado : new String[]{"#", "Producto", "Lote", "Cantidad"}) {
                    tabla.addHeaderCell(new Cell().setBackgroundColor(VERDE_CLARO).setBorder(new SolidBorder(GRIS_BORDE, .6f)).add(new Paragraph(encabezado).setFontSize(8).setBold().setFontColor(VERDE)));
                }
                int i = 1; java.math.BigDecimal total = java.math.BigDecimal.ZERO;
                for (DetalleTransferencia d : t.getDetalles()) {
                    total = total.add(d.getCantidad() == null ? java.math.BigDecimal.ZERO : d.getCantidad());
                    String[] valores = {String.valueOf(i++), d.getProducto() == null ? "-" : d.getProducto().getNombre(), d.getLote() == null ? "-" : d.getLote().getCodigoLote(), cantidad(d.getCantidad())};
                    for (String valor : valores) tabla.addCell(new Cell().setBorder(new SolidBorder(GRIS_BORDE, .4f)).add(new Paragraph(valor).setFontSize(8).setFontColor(GRIS_TEXTO)));
                }
                if (i == 1) tabla.addCell(new Cell(1, 4).setBorder(new SolidBorder(GRIS_BORDE, .4f)).add(new Paragraph("No hay productos registrados").setFontSize(8).setTextAlignment(TextAlignment.CENTER)));
                doc.add(tabla);
                doc.add(new Paragraph("Total de productos: " + (i - 1) + "    |    Total de unidades: " + cantidad(total)).setFontSize(9).setBold().setFontColor(VERDE).setMarginTop(9));
                doc.add(new Paragraph("Documento generado el " + LocalDateTime.now().format(FECHA)).setFontSize(8).setFontColor(GRIS_TEXTO).setTextAlignment(TextAlignment.RIGHT).setMarginTop(22));
            }
            return out.toByteArray();
        } catch (Exception e) { throw new IllegalStateException("No se pudo generar el PDF de la transferencia", e); }
    }
    private Cell celda(String titulo, String valor, boolean resaltada) { return new Cell().setBackgroundColor(resaltada ? VERDE_CLARO : DeviceRgb.WHITE).setBorder(new SolidBorder(GRIS_BORDE, .5f)).add(new Paragraph(titulo.toUpperCase()).setFontSize(7).setBold().setFontColor(VERDE_MEDIO)).add(new Paragraph(valor).setFontSize(9).setFontColor(GRIS_TEXTO).setMarginTop(2)); }
    private String ubicacion(com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen u) { return u == null ? "Sin definir" : texto(u.getAlmacen() == null ? null : u.getAlmacen().getNombre()) + " · " + texto(u.getNombre()) + " (" + texto(u.getCodigo()) + ")"; }
    private String cantidad(java.math.BigDecimal n) { return n == null ? "0" : n.stripTrailingZeros().toPlainString(); }
    private String texto(String s) { return s == null || s.isBlank() ? "-" : s; }
}
