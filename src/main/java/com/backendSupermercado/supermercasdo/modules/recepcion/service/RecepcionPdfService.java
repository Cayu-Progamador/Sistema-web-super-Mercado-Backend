package com.backendSupermercado.supermercasdo.modules.recepcion.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionPdfDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionPdfDto.DetallePdfDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionPdfDto.EmpresaDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionPdfDto.ProveedorPdfDto;
import com.backendSupermercado.supermercasdo.modules.recepcion.dto.RecepcionPdfDto.RecepcionInnerDto;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

@Service
public class RecepcionPdfService {

    private static final Color VERDE_PRINCIPAL = new DeviceRgb(27, 94, 32);
    private static final Color VERDE_SECUNDARIO = new DeviceRgb(46, 125, 50);
    private static final Color GRIS_BORDE = new DeviceRgb(200, 200, 200);
    private static final Color GRIS_TEXTO = new DeviceRgb(100, 100, 100);
    private static final Color BLANCO = ColorConstants.WHITE;
    private static final Color NEGRO = ColorConstants.BLACK;
    private static final Color VERDE_FONDO = new DeviceRgb(232, 245, 233);
    private static final Color ROJO = new DeviceRgb(198, 40, 40);

    private static final float MARGEN_IZQUIERDO = 25f;
    private static final float MARGEN_DERECHO = 25f;
    private static final float MARGEN_SUPERIOR = 25f;
    private static final float MARGEN_INFERIOR = 25f;
    private static final float ANCHO_CONTENIDO = PageSize.A4.getWidth() - MARGEN_IZQUIERDO - MARGEN_DERECHO;

    private static final Map<String, Color> COLOR_ESTADOS = Map.of(
            "EN_REVISION", new DeviceRgb(30, 136, 229),
            "PARCIAL", new DeviceRgb(230, 81, 0),
            "COMPLETADA", new DeviceRgb(46, 125, 50),
            "RECHAZADA", new DeviceRgb(198, 40, 40),
            "ANULADA", new DeviceRgb(158, 158, 158));

    private static final Map<String, Color> COLOR_DISTRIBUCION = Map.of(
            "PENDIENTE_DE_DISTRIBUIR", new DeviceRgb(158, 158, 158),
            "PARCIALMENTE_DISTRIBUIDA", new DeviceRgb(230, 81, 0),
            "DISTRIBUIDA", new DeviceRgb(46, 125, 50),
            "NO_APLICA", new DeviceRgb(158, 158, 158));

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generarPdf(RecepcionPdfDto dto) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc, PageSize.A4)) {

            document.setMargins(MARGEN_SUPERIOR, MARGEN_DERECHO, MARGEN_INFERIOR, MARGEN_IZQUIERDO);

            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, event -> {
                PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
                crearPiePagina(docEvent.getDocument());
            });

            PdfFont helvetica = PdfFontFactory.createFont("Helvetica", "WinAnsi");
            PdfFont helveticaBold = PdfFontFactory.createFont("Helvetica-Bold", "WinAnsi");

            crearEncabezado(document, dto, helvetica, helveticaBold);
            crearInformacionRecepcion(document, dto, helvetica, helveticaBold);
            crearTablaProductos(document, dto, helvetica, helveticaBold);
            crearObservaciones(document, dto, helvetica, helveticaBold);
            crearFirmas(document, dto, helvetica, helveticaBold);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de la recepci\u00f3n", e);
        }

        return baos.toByteArray();
    }

    // =========================================================================
    // ENCABEZADO
    // =========================================================================
    private void crearEncabezado(Document document, RecepcionPdfDto dto,
                                 PdfFont helvetica, PdfFont helveticaBold) {
        EmpresaDto empresa = dto.getEmpresa();

        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{25, 50, 25}));
        headerTable.setWidth(ANCHO_CONTENIDO);

        Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        leftCell.add(crearLogoEmpresa(empresa, helveticaBold));
        headerTable.addCell(leftCell);

        Cell centerCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        Paragraph titulo = new Paragraph("RECEPCI\u00d3N DE MERCADER\u00cdA")
                .setFont(helveticaBold)
                .setFontSize(18)
                .setFontColor(VERDE_PRINCIPAL);
        centerCell.add(titulo);

        Paragraph lineaVerde = new Paragraph("___________________________")
                .setFont(helvetica)
                .setFontSize(10)
                .setFontColor(VERDE_SECUNDARIO);
        centerCell.add(lineaVerde);
        headerTable.addCell(centerCell);

        Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        rightCell.add(new Paragraph("Fecha: " + LocalDateTime.now().format(FECHA_HORA))
                .setFont(helvetica).setFontSize(8).setFontColor(GRIS_TEXTO));
        rightCell.add(new Paragraph("Generado por: " + (dto.getGeneradoPor() != null ? dto.getGeneradoPor() : "Sistema"))
                .setFont(helvetica).setFontSize(8).setFontColor(GRIS_TEXTO));
        headerTable.addCell(rightCell);

        document.add(headerTable);
        document.add(new Paragraph("\n"));
    }

    private Div crearLogoEmpresa(EmpresaDto empresa, PdfFont helveticaBold) {
        Div div = new Div();
        Image logo = cargarImagen(empresa.getLogoPath(), 40f, 40f);
        if (logo != null) {
            logo.setHorizontalAlignment(HorizontalAlignment.LEFT);
            div.add(logo);
        }
        div.add(new Paragraph(empresa.getNombre())
                .setFont(helveticaBold).setFontSize(10).setFontColor(VERDE_PRINCIPAL).setMargin(0));
        return div;
    }

    // =========================================================================
    // SECCIÓN 1: INFORMACIÓN DE LA RECEPCIÓN
    // =========================================================================
    private void crearInformacionRecepcion(Document document, RecepcionPdfDto dto,
                                           PdfFont helvetica, PdfFont helveticaBold) {
        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{100}));
        wrapper.setWidth(ANCHO_CONTENIDO);
        wrapper.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        wrapper.setMarginBottom(10);

        Cell headerCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        headerCell.add(crearTituloSeccion("INFORMACI\u00d3N DE LA RECEPCI\u00d3N", helveticaBold));
        wrapper.addCell(headerCell);

        Table content = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        content.setWidth(UnitValue.createPercentValue(100));

        Cell leftCol = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
        RecepcionInnerDto r = dto.getRecepcion();
        leftCol.add(crearLineaDato("N\u00famero:", r.getNumero(), helvetica, helveticaBold));
        leftCol.add(crearLineaDato("N\u00ba Pedido:", r.getNumeroPedido(), helvetica, helveticaBold));
        leftCol.add(crearLineaDato("Fecha Recepci\u00f3n:", r.getFechaRecepcion() != null
                ? r.getFechaRecepcion().format(FECHA_HORA) : "-", helvetica, helveticaBold));
        leftCol.add(crearLineaDato("Usuario:", r.getUsuario(), helvetica, helveticaBold));
        leftCol.add(crearLineaEstado("Estado:", r.getEstado(), COLOR_ESTADOS, helvetica, helveticaBold));
        leftCol.add(crearLineaEstado("Distribuci\u00f3n:", r.getDistribucion(), COLOR_DISTRIBUCION,
                helvetica, helveticaBold));
        content.addCell(leftCol);

        Cell rightCol = new Cell().setBorder(Border.NO_BORDER).setPadding(5)
                .setVerticalAlignment(VerticalAlignment.TOP);
        ProveedorPdfDto prov = dto.getProveedor();
        if (prov != null) {
            rightCol.add(crearLineaDato("Proveedor:", prov.getNombre(), helvetica, helveticaBold));
            if (prov.getDocumento() != null && !prov.getDocumento().isBlank()) {
                rightCol.add(crearLineaDato("Documento:", prov.getDocumento(), helvetica, helveticaBold));
            }
            if (prov.getDireccion() != null && !prov.getDireccion().isBlank()) {
                rightCol.add(crearLineaDato("Direcci\u00f3n:", prov.getDireccion(), helvetica, helveticaBold));
            }
            if (prov.getTelefono() != null && !prov.getTelefono().isBlank()) {
                rightCol.add(crearLineaDato("Tel\u00e9fono:", prov.getTelefono(), helvetica, helveticaBold));
            }
            if (prov.getCorreo() != null && !prov.getCorreo().isBlank()) {
                rightCol.add(crearLineaDato("Correo:", prov.getCorreo(), helvetica, helveticaBold));
            }
        } else {
            rightCol.add(crearLineaDato("Proveedor:", "-", helvetica, helveticaBold));
        }

        if ("ANULADA".equals(r.getEstado())) {
            rightCol.add(new Paragraph("\n"));
            rightCol.add(crearLineaRoja("Anulada por:", r.getUsuarioAnulacion(), helvetica, helveticaBold));
            rightCol.add(crearLineaRoja("Fecha anulaci\u00f3n:", r.getFechaAnulacion() != null
                    ? r.getFechaAnulacion().format(FECHA_HORA) : "-", helvetica, helveticaBold));
        }
        content.addCell(rightCol);

        wrapper.addCell(new Cell().setBorder(Border.NO_BORDER).add(content));
        document.add(wrapper);
    }

    // =========================================================================
    // SECCIÓN 2: TABLA DE PRODUCTOS
    // =========================================================================
    private void crearTablaProductos(Document document, RecepcionPdfDto dto,
                                     PdfFont helvetica, PdfFont helveticaBold) {
        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{100}));
        wrapper.setWidth(ANCHO_CONTENIDO);
        wrapper.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        wrapper.setMarginBottom(10);

        Cell headerCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        headerCell.add(crearTituloSeccion("DETALLE DE LA RECEPCI\u00d3N", helveticaBold));
        wrapper.addCell(headerCell);

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{4, 17, 9, 9, 8, 8, 8, 8, 9, 20}));
        tabla.setWidth(UnitValue.createPercentValue(100));

        String[] encabezados = {"#", "Producto", "Lote", "Vencimiento", "Solic.", "Recib.", "Acept.",
                "Rechaz.", "Precio", "Subtotal"};
        for (String encabezado : encabezados) {
            Cell enc = new Cell().setBackgroundColor(VERDE_FONDO).setPadding(3)
                    .setBorder(new SolidBorder(GRIS_BORDE, 0.4f));
            enc.add(new Paragraph(encabezado)
                    .setFont(helveticaBold).setFontSize(7).setFontColor(VERDE_PRINCIPAL).setMargin(0)
                    .setTextAlignment(TextAlignment.CENTER));
            tabla.addCell(enc);
        }

        int fila = 1;
        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            for (DetallePdfDto det : dto.getDetalles()) {
                tabla.addCell(crearCelda(String.valueOf(fila++), helvetica, TextAlignment.CENTER));
                tabla.addCell(crearCelda(det.getProducto(), helvetica, TextAlignment.LEFT));
                tabla.addCell(crearCelda(det.getLote() != null ? det.getLote() : "-", helvetica,
                        TextAlignment.CENTER));
                tabla.addCell(crearCelda(det.getVencimiento() != null ? det.getVencimiento() : "-",
                        helvetica, TextAlignment.CENTER));
                tabla.addCell(crearCelda(formatoNumero(det.getSolicitado()), helvetica, TextAlignment.CENTER));
                tabla.addCell(crearCelda(formatoNumero(det.getRecibido()), helvetica, TextAlignment.CENTER));
                tabla.addCell(crearCelda(formatoNumero(det.getAceptado()), helvetica, TextAlignment.CENTER));
                tabla.addCell(crearCeldaRechazo(det, helvetica));
                tabla.addCell(crearCelda("Bs " + formatoNumero(det.getPrecio()), helvetica, TextAlignment.CENTER));
                tabla.addCell(crearCelda("Bs " + formatoNumero(det.getSubtotal()), helvetica, TextAlignment.RIGHT));
            }
        } else {
            Cell vacio = new Cell(1, 10).setPadding(6).setBorder(new SolidBorder(GRIS_BORDE, 0.4f));
            vacio.add(new Paragraph("Esta recepci\u00f3n no tiene productos registrados.")
                    .setFont(helvetica).setFontSize(8).setFontColor(GRIS_TEXTO).setMargin(0)
                    .setTextAlignment(TextAlignment.CENTER));
            tabla.addCell(vacio);
        }

        wrapper.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(4).add(tabla));

        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            RecepcionInnerDto r = dto.getRecepcion();
            Cell resumenCell = new Cell().setBorder(Border.NO_BORDER).setPaddingTop(4);
            resumenCell.add(crearLineaDato("Total de productos:", String.valueOf(r.getTotalProductos() != null
                    ? r.getTotalProductos() : dto.getDetalles().size()), helvetica, helveticaBold));
            resumenCell.add(crearLineaTotal("TOTAL GENERAL:", "Bs " + formatoNumero(r.getTotalGeneral()),
                    helvetica, helveticaBold));
            wrapper.addCell(resumenCell);
        }

        document.add(wrapper);
    }

    private Cell crearCelda(String texto, PdfFont helvetica, TextAlignment alineacion) {
        Cell celda = new Cell().setPadding(3).setBorder(new SolidBorder(GRIS_BORDE, 0.4f));
        celda.add(new Paragraph(texto != null ? texto : "-")
                .setFont(helvetica).setFontSize(7).setFontColor(NEGRO).setMargin(0)
                .setTextAlignment(alineacion));
        return celda;
    }

    private Cell crearCeldaRechazo(DetallePdfDto det, PdfFont helvetica) {
        Cell celda = new Cell().setPadding(3).setBorder(new SolidBorder(GRIS_BORDE, 0.4f));
        Paragraph p = new Paragraph(formatoNumero(det.getRechazado()))
                .setFont(helvetica).setFontSize(7).setFontColor(NEGRO).setMargin(0)
                .setTextAlignment(TextAlignment.CENTER);
        celda.add(p);
        if (det.getRechazado() != null && det.getRechazado().compareTo(BigDecimal.ZERO) > 0
                && det.getMotivo() != null && !det.getMotivo().isBlank()) {
            celda.add(new Paragraph("(" + det.getMotivo() + ")")
                    .setFont(helvetica).setFontSize(5.5f).setFontColor(ROJO).setMargin(0)
                    .setTextAlignment(TextAlignment.CENTER));
        }
        return celda;
    }

    // =========================================================================
    // SECCIÓN 3: OBSERVACIONES
    // =========================================================================
    private void crearObservaciones(Document document, RecepcionPdfDto dto,
                                    PdfFont helvetica, PdfFont helveticaBold) {
        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{100}));
        wrapper.setWidth(ANCHO_CONTENIDO);
        wrapper.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        wrapper.setMarginBottom(10);

        Cell headerCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        headerCell.add(crearTituloSeccion("OBSERVACIONES", helveticaBold));
        wrapper.addCell(headerCell);

        Cell contentCell = new Cell().setBorder(Border.NO_BORDER).setPadding(8);
        String obs = (dto.getRecepcion() != null && dto.getRecepcion().getObservacion() != null
                && !dto.getRecepcion().getObservacion().isBlank())
                ? dto.getRecepcion().getObservacion()
                : "Ninguna observaci\u00f3n registrada.";
        contentCell.add(new Paragraph(obs)
                .setFont(helvetica).setFontSize(9).setFontColor(NEGRO).setMargin(0));
        wrapper.addCell(contentCell);
        document.add(wrapper);
    }

    // =========================================================================
    // SECCIÓN 4: FIRMAS
    // =========================================================================
    private void crearFirmas(Document document, RecepcionPdfDto dto,
                             PdfFont helvetica, PdfFont helveticaBold) {
        Table firmasTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        firmasTable.setWidth(ANCHO_CONTENIDO);
        firmasTable.setMarginTop(10);

        Cell empCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);
        empCell.add(new Paragraph("________________________________")
                .setFont(helvetica).setFontSize(10).setMargin(0));
        empCell.add(new Paragraph("Registrado por")
                .setFont(helveticaBold).setFontSize(9).setFontColor(VERDE_PRINCIPAL).setMargin(0));
        String empresaNombre = dto.getEmpresa() != null ? dto.getEmpresa().getNombre() : "";
        empCell.add(new Paragraph(empresaNombre)
                .setFont(helvetica).setFontSize(8).setFontColor(GRIS_TEXTO).setMargin(0));
        if (dto.getRecepcion() != null && dto.getRecepcion().getUsuario() != null) {
            empCell.add(new Paragraph("Usuario: " + dto.getRecepcion().getUsuario())
                    .setFont(helvetica).setFontSize(7).setFontColor(GRIS_TEXTO).setMargin(0));
        }
        firmasTable.addCell(empCell);

        Cell provCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);
        provCell.add(new Paragraph("________________________________")
                .setFont(helvetica).setFontSize(10).setMargin(0));
        provCell.add(new Paragraph("Recibido por")
                .setFont(helveticaBold).setFontSize(9).setFontColor(VERDE_PRINCIPAL).setMargin(0));
        String proveedorNombre = dto.getProveedor() != null ? dto.getProveedor().getNombre() : "";
        provCell.add(new Paragraph(proveedorNombre)
                .setFont(helvetica).setFontSize(8).setFontColor(GRIS_TEXTO).setMargin(0));
        firmasTable.addCell(provCell);

        document.add(firmasTable);

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Nota: Documento de car\u00e1cter informativo, no constituye factura comercial.")
                .setFont(helvetica).setFontSize(7).setFontColor(GRIS_TEXTO)
                .setTextAlignment(TextAlignment.CENTER).setMargin(0));
    }

    // =========================================================================
    // PIE DE PÁGINA
    // =========================================================================
    private void crearPiePagina(PdfDocument pdfDoc) {
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getLastPage());
        Rectangle pageRect = pdfDoc.getDefaultPageSize();

        float y = MARGEN_INFERIOR - 5;
        float pageWidth = pageRect.getWidth();

        canvas.setStrokeColor(VERDE_SECUNDARIO)
                .setLineWidth(1f)
                .moveTo(MARGEN_IZQUIERDO, y + 15)
                .lineTo(pageWidth - MARGEN_DERECHO, y + 15)
                .stroke();

        try {
            PdfFont helvetica = PdfFontFactory.createFont("Helvetica", "WinAnsi");

            canvas.beginText()
                    .setFontAndSize(helvetica, 8)
                    .setFillColor(GRIS_TEXTO)
                    .moveText(MARGEN_IZQUIERDO, y)
                    .showText("Este documento fue generado autom\u00e1ticamente por el sistema ERP y tiene validez administrativa.")
                    .endText();

            int pageNum = pdfDoc.getPageNumber(pdfDoc.getLastPage());
            int totalPages = pdfDoc.getNumberOfPages();
            String pageStr = "P\u00e1gina " + pageNum + " de " + totalPages;

            float textWidth = helvetica.getWidth(pageStr, 8);
            canvas.beginText()
                    .setFontAndSize(helvetica, 8)
                    .setFillColor(GRIS_TEXTO)
                    .moveText(pageWidth - MARGEN_DERECHO - textWidth, y)
                    .showText(pageStr)
                    .endText();
        } catch (IOException e) {
            // Ignorar error en pie de página
        }
    }

    // =========================================================================
    // UTILIDADES
    // =========================================================================
    private Div crearTituloSeccion(String titulo, PdfFont helveticaBold) {
        Table titleBg = new Table(UnitValue.createPercentArray(new float[]{100}));
        titleBg.setWidth(UnitValue.createPercentValue(100));

        Cell titleCell = new Cell().setBackgroundColor(VERDE_PRINCIPAL).setPadding(6).setBorder(Border.NO_BORDER);
        titleCell.add(new Paragraph(titulo)
                .setFont(helveticaBold).setFontSize(10).setFontColor(BLANCO).setMargin(0));
        titleBg.addCell(titleCell);

        Div div = new Div();
        div.add(titleBg);
        return div;
    }

    private Paragraph crearLineaDato(String label, String value,
                                     PdfFont helvetica, PdfFont helveticaBold) {
        Text labelText = new Text(label + " ")
                .setFont(helveticaBold).setFontSize(8).setFontColor(VERDE_SECUNDARIO);
        Text valueText = new Text(value != null ? value : "-")
                .setFont(helvetica).setFontSize(8).setFontColor(NEGRO);
        return new Paragraph().add(labelText).add(valueText)
                .setMargin(0).setMarginBottom(3).setFixedLeading(12);
    }

    private Paragraph crearLineaEstado(String label, String valor, Map<String, Color> colores,
                                       PdfFont helvetica, PdfFont helveticaBold) {
        Text labelText = new Text(label + " ")
                .setFont(helveticaBold).setFontSize(8).setFontColor(VERDE_SECUNDARIO);
        String valorLimpio = valor != null ? valor.replace('_', ' ') : "-";
        Color color = colores.getOrDefault(valor != null ? valor.toUpperCase() : "", GRIS_TEXTO);
        Text valueText = new Text(valorLimpio)
                .setFont(helveticaBold).setFontSize(8).setFontColor(color);
        return new Paragraph().add(labelText).add(valueText)
                .setMargin(0).setMarginBottom(3).setFixedLeading(12);
    }

    private Paragraph crearLineaRoja(String label, String value,
                                     PdfFont helvetica, PdfFont helveticaBold) {
        Text labelText = new Text(label + " ")
                .setFont(helveticaBold).setFontSize(8).setFontColor(ROJO);
        Text valueText = new Text(value != null ? value : "-")
                .setFont(helveticaBold).setFontSize(8).setFontColor(ROJO);
        return new Paragraph().add(labelText).add(valueText)
                .setMargin(0).setMarginBottom(3).setFixedLeading(12);
    }

    private Paragraph crearLineaTotal(String label, String value,
                                      PdfFont helvetica, PdfFont helveticaBold) {
        Text labelText = new Text(label + " ")
                .setFont(helveticaBold).setFontSize(9).setFontColor(VERDE_PRINCIPAL);
        Text valueText = new Text(value != null ? value : "-")
                .setFont(helveticaBold).setFontSize(9).setFontColor(VERDE_PRINCIPAL);
        return new Paragraph().add(labelText).add(valueText)
                .setMargin(0).setMarginTop(3).setFixedLeading(14);
    }

    private String formatoNumero(BigDecimal valor) {
        return valor != null ? valor.stripTrailingZeros().toPlainString() : "-";
    }

    private Image cargarImagen(String ruta, float maxWidth, float maxHeight) {
        if (ruta == null || ruta.isBlank()) return null;
        try {
            Path path = Paths.get(ruta);
            if (!path.isAbsolute()) {
                path = Paths.get(System.getProperty("user.dir"), ruta);
            }
            if (Files.exists(path)) {
                Image img = new Image(ImageDataFactory.create(path.toAbsolutePath().toString()));
                img.scaleToFit(maxWidth, maxHeight);
                return img;
            }
        } catch (Exception e) {
            // Si no se puede cargar la imagen, retornamos null
        }
        return null;
    }
}