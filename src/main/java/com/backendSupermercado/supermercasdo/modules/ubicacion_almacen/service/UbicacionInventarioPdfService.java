package com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.backendSupermercado.supermercasdo.modules.ubicacion_almacen.entity.UbicacionAlmacen;
import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

@Service
public class UbicacionInventarioPdfService {
    private static final DeviceRgb VERDE = new DeviceRgb(27, 94, 32);
    private static final DeviceRgb VERDE_CLARO = new DeviceRgb(232, 245, 233);
    private static final DeviceRgb BORDE = new DeviceRgb(205, 215, 202);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public byte[] generar(UbicacionAlmacen ubicacion, List<Existencia> existencias, String usuario) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer); Document doc = new Document(pdf, PageSize.A4.rotate())) {
            doc.setMargins(28, 25, 28, 25);
            doc.add(new Paragraph("SUPERMERCADO").setFontSize(11).setBold().setFontColor(VERDE));
            doc.add(new Paragraph("INVENTARIO POR UBICACIÓN").setFontSize(18).setBold().setFontColor(VERDE).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph(ubicacion.getNombre() + "  ·  " + ubicacion.getCodigo() + "  ·  " + ubicacion.getAlmacen().getNombre())
                    .setFontSize(11).setBold().setFontColor(VERDE).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("Estado: " + (Boolean.TRUE.equals(ubicacion.getActivo()) ? "ACTIVO" : "INACTIVO")
                    + "    Tipo: " + texto(ubicacion.getTipo()) + "    Generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    + "    Usuario: " + texto(usuario)).setFontSize(8));
            doc.add(new Paragraph(texto(ubicacion.getDescripcion())).setFontSize(9).setMarginBottom(10));
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 22, 13, 10, 10, 14, 15})).useAllAvailableWidth();
            for (String h : new String[]{"#", "Producto", "Lote", "Cantidad", "Unidad", "Vencimiento", "Estado"}) {
                table.addHeaderCell(new Cell().setBackgroundColor(VERDE_CLARO).setBorder(new com.itextpdf.layout.borders.SolidBorder(BORDE, .5f)).add(new Paragraph(h).setBold().setFontColor(VERDE).setFontSize(8)));
            }
            int i = 1;
            for (Existencia e : existencias) {
                String venc = e.getLote() != null && e.getLote().getFechaVencimiento() != null ? e.getLote().getFechaVencimiento().format(FECHA) : "-";
                String estado = e.getLote() != null && e.getLote().getFechaVencimiento() != null && e.getLote().getFechaVencimiento().isBefore(LocalDate.now()) ? "VENCIDO" : "DISPONIBLE";
                String[] vals = {String.valueOf(i++), e.getProducto().getNombre(), e.getLote() == null ? "-" : e.getLote().getCodigoLote(), e.getCantidad().stripTrailingZeros().toPlainString(), e.getProducto().getUnidadBase() == null ? "-" : e.getProducto().getUnidadBase().getAbreviatura(), venc, estado};
                for (String v : vals) table.addCell(new Cell().setBorder(new com.itextpdf.layout.borders.SolidBorder(BORDE, .4f)).add(new Paragraph(texto(v)).setFontSize(7)));
            }
            if (existencias.isEmpty()) table.addCell(new Cell(1, 7).add(new Paragraph("No existen productos disponibles en esta ubicación.").setFontSize(8).setTextAlignment(TextAlignment.CENTER)));
            doc.add(table);
            doc.add(new Paragraph("Total de productos: " + existencias.size()).setFontSize(8).setMarginTop(8));
        } catch (Exception e) { throw new IllegalStateException("No se pudo generar el PDF de la ubicación", e); }
        return out.toByteArray();
    }
    private String texto(String s) { return s == null || s.isBlank() ? "-" : s; }
}
