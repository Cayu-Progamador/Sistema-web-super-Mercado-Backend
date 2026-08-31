package com.backendSupermercado.supermercasdo.modules.devoluciones.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.DevolucionProveedorResponseDto;
import com.backendSupermercado.supermercasdo.modules.devoluciones.dto.DevolucionProveedorResponseDto.DetalleDto;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class DevolucionPdfService {

    private static final Color VERDE = new DeviceRgb(27, 94, 32);
    private static final Color VERDE_MEDIO = new DeviceRgb(46, 125, 50);
    private static final Color VERDE_FONDO = new DeviceRgb(232, 245, 233);
    private static final Color GRIS = new DeviceRgb(100, 100, 100);
    private static final Color BORDE = new DeviceRgb(205, 215, 205);
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Map<String, Color> COLORES_ESTADO = Map.of(
            "BORRADOR", new DeviceRgb(158, 158, 158),
            "CONFIRMADA", new DeviceRgb(25, 118, 210),
            "ENVIADA", new DeviceRgb(245, 124, 0),
            "EN_PROCESO", new DeviceRgb(123, 31, 162),
            "COMPLETADA", VERDE_MEDIO,
            "CANCELADA", new DeviceRgb(91, 102, 117));

    public byte[] generar(DevolucionProveedorResponseDto dto) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(salida);
             PdfDocument pdf = new PdfDocument(writer);
             Document documento = new Document(pdf, PageSize.A4)) {
            documento.setMargins(28, 25, 32, 25);
            PdfFont normal = PdfFontFactory.createFont("Helvetica", "WinAnsi");
            PdfFont negrita = PdfFontFactory.createFont("Helvetica-Bold", "WinAnsi");
            pdf.addEventHandler(PdfDocumentEvent.END_PAGE, evento -> piePagina((PdfDocumentEvent) evento, normal));

            encabezado(documento, dto, normal, negrita);
            informacion(documento, dto, normal, negrita);
            productos(documento, dto, normal, negrita);
            observacion(documento, dto, normal, negrita);
            firmas(documento, normal, negrita);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de la devolución", e);
        }
        return salida.toByteArray();
    }

    private void encabezado(Document doc, DevolucionProveedorResponseDto dto, PdfFont normal, PdfFont bold) {
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{25, 50, 25})).useAllAvailableWidth();
        tabla.addCell(sinBorde("SUPERMERCADO\nLA MARTITA", bold, 10, VERDE).setTextAlignment(TextAlignment.LEFT));
        Cell centro = sinBorde("DEVOLUCIÓN AL PROVEEDOR", bold, 18, VERDE).setTextAlignment(TextAlignment.CENTER);
        centro.add(new Paragraph("___________________________").setFont(normal).setFontSize(9).setFontColor(VERDE_MEDIO));
        tabla.addCell(centro);
        tabla.addCell(sinBorde("Generado: " + LocalDateTime.now().format(FECHA_HORA), normal, 8, GRIS)
                .setTextAlignment(TextAlignment.RIGHT));
        doc.add(tabla);
        doc.add(new Paragraph(" ").setMargin(0));
    }

    private void informacion(Document doc, DevolucionProveedorResponseDto dto, PdfFont normal, PdfFont bold) {
        Table bloque = bloque("INFORMACIÓN DE LA DEVOLUCIÓN", bold);
        Table datos = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        Cell izquierda = new Cell().setBorder(Border.NO_BORDER).setPadding(7);
        izquierda.add(linea("Número:", dto.getNumero(), normal, bold));
        izquierda.add(linea("Recepción:", dto.getNumeroRecepcion(), normal, bold));
        izquierda.add(linea("Fecha:", dto.getFechaDevolucion() != null ? dto.getFechaDevolucion().format(FECHA_HORA) : "-", normal, bold));
        izquierda.add(lineaEstado("Estado:", dto.getEstado(), normal, bold));
        Cell derecha = new Cell().setBorder(Border.NO_BORDER).setPadding(7);
        derecha.add(linea("Proveedor:", dto.getProveedor(), normal, bold));
        derecha.add(linea("Usuario responsable:", dto.getUsuario(), normal, bold));
        derecha.add(linea("Cantidad de productos:", String.valueOf(dto.getDetalles().size()), normal, bold));
        datos.addCell(izquierda);
        datos.addCell(derecha);
        bloque.addCell(new Cell().setBorder(Border.NO_BORDER).add(datos));
        doc.add(bloque);
    }

    private void productos(Document doc, DevolucionProveedorResponseDto dto, PdfFont normal, PdfFont bold) {
        Table bloque = bloque("DETALLE DE PRODUCTOS DEVUELTOS", bold);
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{4, 23, 12, 22, 10, 19, 10})).useAllAvailableWidth();
        for (String titulo : new String[]{"#", "Producto", "Lote", "Origen del stock", "Cantidad", "Motivo", "Observación"}) {
            tabla.addHeaderCell(new Cell().setPadding(4).setBackgroundColor(VERDE_FONDO)
                    .setBorder(new SolidBorder(BORDE, .5f)).add(new Paragraph(titulo).setFont(bold)
                            .setFontSize(7).setFontColor(VERDE).setTextAlignment(TextAlignment.CENTER).setMargin(0)));
        }
        int numero = 1;
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleDto detalle : dto.getDetalles()) {
            String origen = detalle.getOrigenes().stream()
                    .map(o -> o.getAlmacen() + " · " + o.getUbicacion() + " (" + numero(o.getCantidad()) + ")")
                    .collect(Collectors.joining("\n"));
            tabla.addCell(celda(String.valueOf(numero++), normal, TextAlignment.CENTER));
            tabla.addCell(celda(detalle.getProducto(), normal, TextAlignment.LEFT));
            tabla.addCell(celda(valor(detalle.getLote()), normal, TextAlignment.CENTER));
            tabla.addCell(celda(valor(origen), normal, TextAlignment.LEFT));
            tabla.addCell(celda(numero(detalle.getCantidad()), normal, TextAlignment.CENTER));
            tabla.addCell(celda(valor(detalle.getMotivo()), normal, TextAlignment.LEFT));
            tabla.addCell(celda(valor(detalle.getObservacion()), normal, TextAlignment.LEFT));
            total = total.add(detalle.getCantidad());
        }
        bloque.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(0).add(tabla));
        Cell totalCell = new Cell().setBorder(Border.NO_BORDER).setPadding(8).setTextAlignment(TextAlignment.RIGHT);
        totalCell.add(new Paragraph("TOTAL DE UNIDADES: " + numero(total)).setFont(bold).setFontSize(10).setFontColor(VERDE));
        bloque.addCell(totalCell);
        doc.add(bloque);
    }

    private void observacion(Document doc, DevolucionProveedorResponseDto dto, PdfFont normal, PdfFont bold) {
        Table bloque = bloque("OBSERVACIONES", bold);
        bloque.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(8)
                .add(new Paragraph(valor(dto.getObservacion())).setFont(normal).setFontSize(9)));
        doc.add(bloque);
    }

    private void firmas(Document doc, PdfFont normal, PdfFont bold) {
        doc.add(new Paragraph("\n\n"));
        Table firmas = new Table(UnitValue.createPercentArray(new float[]{45, 10, 45})).useAllAvailableWidth();
        firmas.addCell(firma("RESPONSABLE DE DEVOLUCIÓN", normal, bold));
        firmas.addCell(new Cell().setBorder(Border.NO_BORDER));
        firmas.addCell(firma("RECIBIDO POR EL PROVEEDOR", normal, bold));
        doc.add(firmas);
    }

    private Table bloque(String titulo, PdfFont bold) {
        Table bloque = new Table(UnitValue.createPercentArray(new float[]{100})).useAllAvailableWidth()
                .setBorder(new SolidBorder(BORDE, .6f)).setMarginBottom(10);
        bloque.addCell(new Cell().setBorder(Border.NO_BORDER).setBackgroundColor(VERDE)
                .setPadding(6).add(new Paragraph(titulo).setFont(bold).setFontSize(9)
                        .setFontColor(ColorConstants.WHITE).setMargin(0)));
        return bloque;
    }

    private Paragraph linea(String etiqueta, String valor, PdfFont normal, PdfFont bold) {
        return new Paragraph().setMargin(0).setMarginBottom(3).setFontSize(9)
                .add(new com.itextpdf.layout.element.Text(etiqueta + " ").setFont(bold).setFontColor(VERDE))
                .add(new com.itextpdf.layout.element.Text(valor(valor)).setFont(normal));
    }

    private Paragraph lineaEstado(String etiqueta, String estado, PdfFont normal, PdfFont bold) {
        return linea(etiqueta, valor(estado).replace('_', ' '), normal, bold)
                .setFontColor(COLORES_ESTADO.getOrDefault(estado, GRIS));
    }

    private Cell celda(String texto, PdfFont fuente, TextAlignment alineacion) {
        return new Cell().setPadding(4).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(BORDE, .4f)).add(new Paragraph(valor(texto)).setFont(fuente)
                        .setFontSize(7).setTextAlignment(alineacion).setMargin(0));
    }

    private Cell sinBorde(String texto, PdfFont fuente, float tamano, Color color) {
        return new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .add(new Paragraph(texto).setFont(fuente).setFontSize(tamano).setFontColor(color).setMargin(0));
    }

    private Cell firma(String titulo, PdfFont normal, PdfFont bold) {
        Cell celda = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        celda.add(new Paragraph("________________________________").setFont(normal).setFontSize(9));
        celda.add(new Paragraph(titulo).setFont(bold).setFontSize(8).setFontColor(VERDE));
        celda.add(new Paragraph("Nombre, firma y sello").setFont(normal).setFontSize(7).setFontColor(GRIS));
        return celda;
    }

    private void piePagina(PdfDocumentEvent evento, PdfFont fuente) {
        PdfDocument pdf = evento.getDocument();
        int pagina = pdf.getPageNumber(evento.getPage());
        PdfCanvas canvas = new PdfCanvas(evento.getPage().newContentStreamAfter(), evento.getPage().getResources(), pdf);
        canvas.beginText().setFontAndSize(fuente, 7).setColor(GRIS, true)
                .moveText(PageSize.A4.getWidth() / 2 - 25, 15)
                .showText("Página " + pagina + " · Supermercado La Martita").endText().release();
    }

    private String valor(String texto) { return texto == null || texto.isBlank() ? "-" : texto; }
    private String numero(BigDecimal numero) { return numero == null ? "0" : numero.stripTrailingZeros().toPlainString(); }
}
