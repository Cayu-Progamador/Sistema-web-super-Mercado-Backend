package com.backendSupermercado.supermercasdo.modules.inventario_fisico.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.empleado.entity.Persona;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.entity.DetalleInventarioFisico;
import com.backendSupermercado.supermercasdo.modules.inventario_fisico.entity.InventarioFisico;
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
public class InventarioFisicoPdfService {

    private static final Color VERDE = new DeviceRgb(27, 94, 32);
    private static final Color VERDE_CLARO = new DeviceRgb(232, 245, 233);
    private static final Color GRIS_CLARO = new DeviceRgb(247, 249, 246);
    private static final Color BORDE = new DeviceRgb(205, 215, 205);
    private static final Color GRIS = new DeviceRgb(95, 105, 96);
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(InventarioFisico inventario, List<DetalleInventarioFisico> detalles) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(salida);
                PdfDocument pdf = new PdfDocument(writer);
                Document documento = new Document(pdf, PageSize.A4.rotate())) {
            documento.setMargins(24, 22, 26, 22);
            PdfFont normal = PdfFontFactory.createFont("Helvetica", "WinAnsi");
            PdfFont negrita = PdfFontFactory.createFont("Helvetica-Bold", "WinAnsi");

            agregarEncabezado(documento, inventario, normal, negrita);
            agregarInformacion(documento, inventario, normal, negrita);
            agregarResumen(documento, detalles, normal, negrita);
            agregarDetalle(documento, detalles, normal, negrita);
            agregarFirmas(documento, inventario, normal, negrita);
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar el acta del inventario físico", exception);
        }
        return salida.toByteArray();
    }

    private void agregarEncabezado(Document documento, InventarioFisico inventario,
            PdfFont normal, PdfFont negrita) {
        documento.add(new Paragraph("SUPERMERCADO LA MARTITA")
                .setFont(negrita).setFontSize(10).setFontColor(VERDE));
        documento.add(new Paragraph("ACTA DE INVENTARIO FÍSICO")
                .setFont(negrita).setFontSize(18).setFontColor(VERDE)
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(-8).setMarginBottom(2));
        documento.add(new Paragraph("Código: " + inventario.getCodigo() + "   |   Estado: " + inventario.getEstado())
                .setFont(negrita).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMarginBottom(2));
        documento.add(new Paragraph("Generado: " + LocalDateTime.now().format(FECHA_HORA))
                .setFont(normal).setFontSize(8).setFontColor(GRIS)
                .setTextAlignment(TextAlignment.RIGHT).setMarginTop(-15).setMarginBottom(10));
    }

    private void agregarInformacion(Document documento, InventarioFisico i,
            PdfFont normal, PdfFont negrita) {
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{16, 34, 16, 34})).useAllAvailableWidth();
        dato(tabla, "Tipo", etiqueta(i.getTipo()), normal, negrita);
        dato(tabla, "Fecha programada", texto(i.getFechaProgramada()), normal, negrita);
        dato(tabla, "Almacén", i.getAlmacen() == null ? "Todos los almacenes" : i.getAlmacen().getNombre(), normal, negrita);
        dato(tabla, "Ubicación", i.getUbicacion() == null ? "Todas" : i.getUbicacion().getNombre(), normal, negrita);
        dato(tabla, "Categoría", i.getCategoria() == null ? "Todas" : i.getCategoria().getNombre(), normal, negrita);
        dato(tabla, "Responsable", nombreResponsable(i), normal, negrita);
        dato(tabla, "Inicio", fecha(i.getFechaInicio()), normal, negrita);
        dato(tabla, "Finalización", fecha(i.getFechaFinalizacion()), normal, negrita);
        dato(tabla, "Aprobación", fecha(i.getFechaAprobacion()), normal, negrita);
        dato(tabla, "Aprobado por", i.getAprobadoPor() == null ? "-" : i.getAprobadoPor().getUsername(), normal, negrita);
        dato(tabla, "Conteo ciego", Boolean.TRUE.equals(i.getConteoCiego()) ? "Sí" : "No", normal, negrita);
        dato(tabla, "Observación", texto(i.getObservacion()), normal, negrita);
        documento.add(tabla.setMarginBottom(10));
    }

    private void agregarResumen(Document documento, List<DetalleInventarioFisico> detalles,
            PdfFont normal, PdfFont negrita) {
        long contadas = detalles.stream().filter(d -> d.getCantidadFisica() != null).count();
        long diferencias = detalles.stream().filter(d -> signo(d.getDiferencia()) != 0).count();
        BigDecimal entradas = detalles.stream().map(DetalleInventarioFisico::getDiferencia)
                .filter(v -> signo(v) > 0).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal salidas = detalles.stream().map(DetalleInventarioFisico::getDiferencia)
                .filter(v -> signo(v) < 0).map(BigDecimal::abs).reduce(BigDecimal.ZERO, BigDecimal::add);
        Table tabla = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        resumen(tabla, "Líneas", detalles.size(), normal, negrita);
        resumen(tabla, "Contadas", contadas, normal, negrita);
        resumen(tabla, "Con diferencia", diferencias, normal, negrita);
        resumen(tabla, "Entradas", entradas, normal, negrita);
        resumen(tabla, "Salidas", salidas, normal, negrita);
        documento.add(tabla.setMarginBottom(10));
    }

    private void agregarDetalle(Document documento, List<DetalleInventarioFisico> detalles,
            PdfFont normal, PdfFont negrita) {
        String[] columnas = {"Código", "Producto", "Unidad", "Almacén", "Ubicación", "Lote",
                "Vencimiento", "Sistema", "Físico", "Diferencia", "Motivo / observación"};
        float[] anchos = {8, 17, 6, 10, 10, 9, 8, 7, 7, 7, 16};
        Table tabla = new Table(UnitValue.createPercentArray(anchos)).useAllAvailableWidth();
        for (String columna : columnas) tabla.addHeaderCell(celdaCabecera(columna, negrita));
        for (DetalleInventarioFisico d : detalles) {
            celda(tabla, texto(d.getProducto().getCodigoInterno()), normal);
            celda(tabla, texto(d.getProducto().getNombre()), normal);
            celda(tabla, d.getProducto().getUnidadBase() == null ? "-" : texto(d.getProducto().getUnidadBase().getAbreviatura()), normal);
            celda(tabla, texto(d.getAlmacen().getNombre()), normal);
            celda(tabla, texto(d.getUbicacion().getNombre()), normal);
            celda(tabla, d.getLote() == null ? "-" : texto(d.getLote().getCodigoLote()), normal);
            celda(tabla, d.getLote() == null ? "-" : texto(d.getLote().getFechaVencimiento()), normal);
            celda(tabla, texto(d.getCantidadSistema()), normal);
            celda(tabla, texto(d.getCantidadFisica()), normal);
            celda(tabla, texto(d.getDiferencia()), normal);
            celda(tabla, motivo(d), normal);
        }
        documento.add(tabla);
        documento.add(new Paragraph("Referencia Kardex: INV_FISICO:" + detalles.stream().findFirst()
                .map(d -> d.getInventarioFisico().getCodigo()).orElse("-"))
                .setFont(normal).setFontSize(8).setFontColor(GRIS).setMarginTop(7));
    }

    private void agregarFirmas(Document documento, InventarioFisico i, PdfFont normal, PdfFont negrita) {
        Table firmas = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth().setMarginTop(24);
        firma(firmas, nombreResponsable(i), "Responsable del conteo", normal, negrita);
        firma(firmas, "", "Revisor", normal, negrita);
        firma(firmas, i.getAprobadoPor() == null ? "" : i.getAprobadoPor().getUsername(), "Administrador", normal, negrita);
        documento.add(firmas);
    }

    private void dato(Table tabla, String titulo, String valor, PdfFont normal, PdfFont negrita) {
        tabla.addCell(base(GRIS_CLARO).add(new Paragraph(titulo).setFont(negrita).setFontSize(7).setFontColor(VERDE)));
        tabla.addCell(base(null).add(new Paragraph(texto(valor)).setFont(normal).setFontSize(7)));
    }

    private void resumen(Table tabla, String titulo, Object valor, PdfFont normal, PdfFont negrita) {
        tabla.addCell(base(VERDE_CLARO).setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph(titulo).setFont(normal).setFontSize(7).setFontColor(GRIS).setMarginBottom(2))
                .add(new Paragraph(texto(valor)).setFont(negrita).setFontSize(11).setFontColor(VERDE)));
    }

    private Cell celdaCabecera(String valor, PdfFont negrita) {
        return base(VERDE_CLARO).setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph(valor).setFont(negrita).setFontSize(6.5f).setFontColor(VERDE).setMargin(0));
    }

    private void celda(Table tabla, String valor, PdfFont normal) {
        tabla.addCell(base(null).add(new Paragraph(valor).setFont(normal).setFontSize(6).setMargin(0)));
    }

    private void firma(Table tabla, String nombre, String cargo, PdfFont normal, PdfFont negrita) {
        tabla.addCell(new Cell().setBorder(null).setPadding(8).setTextAlignment(TextAlignment.CENTER)
                .add(new Paragraph("________________________________").setFont(normal).setFontSize(8))
                .add(new Paragraph(texto(nombre)).setFont(negrita).setFontSize(8))
                .add(new Paragraph(cargo).setFont(normal).setFontSize(7).setFontColor(GRIS)));
    }

    private Cell base(Color fondo) {
        Cell cell = new Cell().setPadding(4).setBorder(new SolidBorder(BORDE, .5f));
        return fondo == null ? cell : cell.setBackgroundColor(fondo);
    }

    private String nombreResponsable(InventarioFisico i) {
        if (i.getResponsable() == null || i.getResponsable().getPersona() == null) return "-";
        Persona p = i.getResponsable().getPersona();
        return (texto(p.getNombres()) + " " + textoVacio(p.getApellidoPaterno()) + " "
                + textoVacio(p.getApellidoMaterno())).trim().replaceAll("\\s+", " ");
    }

    private String motivo(DetalleInventarioFisico d) {
        String motivo = texto(d.getMotivoDiferencia());
        return d.getObservacion() == null || d.getObservacion().isBlank()
                ? motivo : motivo + " - " + d.getObservacion().trim();
    }

    private String etiqueta(Object tipo) {
        return tipo == null ? "-" : tipo.toString().replace('_', ' ');
    }

    private String fecha(LocalDateTime valor) {
        return valor == null ? "-" : valor.format(FECHA_HORA);
    }

    private static int signo(BigDecimal valor) {
        return valor == null ? 0 : valor.signum();
    }

    private static String texto(Object valor) {
        if (valor == null || valor.toString().isBlank()) return "-";
        if (valor instanceof BigDecimal numero) {
            return numero.stripTrailingZeros().toPlainString();
        }
        return valor.toString();
    }

    private static String textoVacio(String valor) {
        return valor == null ? "" : valor;
    }
}
