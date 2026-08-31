package com.backendSupermercado.supermercasdo.modules.pedidos.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoPdfDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoPdfDto.DetallePdfDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoPdfDto.EmpresaDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoPdfDto.PedidoPdfInnerDto;
import com.backendSupermercado.supermercasdo.modules.pedidos.dto.PedidoPdfDto.ProveedorPdfDto;
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
public class PedidoPdfService {

    private static final Color VERDE_PRINCIPAL = new DeviceRgb(27, 94, 32);
    private static final Color VERDE_SECUNDARIO = new DeviceRgb(46, 125, 50);
    private static final Color GRIS_BORDE = new DeviceRgb(200, 200, 200);
    private static final Color GRIS_TEXTO = new DeviceRgb(100, 100, 100);
    private static final Color BLANCO = ColorConstants.WHITE;
    private static final Color NEGRO = ColorConstants.BLACK;
    private static final Color VERDE_FONDO = new DeviceRgb(232, 245, 233);

    private static final float MARGEN_IZQUIERDO = 25f;
    private static final float MARGEN_DERECHO = 25f;
    private static final float MARGEN_SUPERIOR = 25f;
    private static final float MARGEN_INFERIOR = 25f;
    private static final float ANCHO_CONTENIDO = PageSize.A4.getWidth() - MARGEN_IZQUIERDO - MARGEN_DERECHO;

    private static final Map<String, Color> COLOR_ESTADOS = Map.of(
            "BORRADOR", new DeviceRgb(158, 158, 158),
            "ENVIADO", new DeviceRgb(30, 136, 229),
            "PARCIALMENTE_RECIBIDO", new DeviceRgb(230, 81, 0),
            "COMPLETADO", new DeviceRgb(46, 125, 50),
            "CANCELADO", new DeviceRgb(198, 40, 40));

    public byte[] generarPdf(PedidoPdfDto dto) {
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
            crearInformacionPedido(document, dto, helvetica, helveticaBold);
            crearTablaProductos(document, dto, helvetica, helveticaBold);
            crearObservaciones(document, dto, helvetica, helveticaBold);
            crearFirmas(document, dto, helvetica, helveticaBold);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF del pedido", e);
        }

        return baos.toByteArray();
    }

    // =========================================================================
    // ENCABEZADO
    // =========================================================================
    private void crearEncabezado(Document document, PedidoPdfDto dto,
                                 PdfFont helvetica, PdfFont helveticaBold) {
        EmpresaDto empresa = dto.getEmpresa();

        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{25, 50, 25}));
        headerTable.setWidth(ANCHO_CONTENIDO);

        // Columna izquierda: Logo + Empresa
        Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        leftCell.add(crearLogoEmpresa(empresa, helveticaBold));
        headerTable.addCell(leftCell);

        // Columna central: Título
        Cell centerCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        Paragraph titulo = new Paragraph("ORDEN DE PEDIDO")
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

        // Columna derecha: Fecha/Hora
        Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        rightCell.add(new Paragraph("Fecha: " + fechaHora)
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
    // SECCIÓN 1: INFORMACIÓN DEL PEDIDO
    // =========================================================================
    private void crearInformacionPedido(Document document, PedidoPdfDto dto,
                                        PdfFont helvetica, PdfFont helveticaBold) {
        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{100}));
        wrapper.setWidth(ANCHO_CONTENIDO);
        wrapper.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        wrapper.setMarginBottom(10);

        Cell headerCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        headerCell.add(crearTituloSeccion("INFORMACI\u00d3N DEL PEDIDO", helveticaBold));
        wrapper.addCell(headerCell);

        Table content = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        content.setWidth(UnitValue.createPercentValue(100));

        // Columna izquierda: datos del pedido
        Cell leftCol = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
        PedidoPdfInnerDto p = dto.getPedido();
        leftCol.add(crearLineaDato("N\u00famero:", p.getNumero(), helvetica, helveticaBold));
        leftCol.add(crearLineaDato("Fecha Pedido:", p.getFechaPedido() != null
                ? p.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-",
                helvetica, helveticaBold));
        leftCol.add(crearLineaDato("Entrega Estimada:", p.getFechaEntregaEstimada() != null
                ? p.getFechaEntregaEstimada().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-",
                helvetica, helveticaBold));
        leftCol.add(crearLineaDato("Usuario:", p.getUsuario(), helvetica, helveticaBold));
        leftCol.add(crearLineaEstado("Estado:", p.getEstado(), helvetica, helveticaBold));
        content.addCell(leftCol);

        // Columna derecha: proveedor
        Cell rightCol = new Cell().setBorder(Border.NO_BORDER).setPadding(5)
                .setVerticalAlignment(VerticalAlignment.TOP);
        ProveedorPdfDto prov = dto.getProveedor();
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
        content.addCell(rightCol);

        wrapper.addCell(new Cell().setBorder(Border.NO_BORDER).add(content));
        document.add(wrapper);
    }

    // =========================================================================
    // SECCIÓN 2: TABLA DE PRODUCTOS
    // =========================================================================
    private void crearTablaProductos(Document document, PedidoPdfDto dto,
                                     PdfFont helvetica, PdfFont helveticaBold) {
        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{100}));
        wrapper.setWidth(ANCHO_CONTENIDO);
        wrapper.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        wrapper.setMarginBottom(10);

        Cell headerCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        headerCell.add(crearTituloSeccion("PRODUCTOS SOLICITADOS", helveticaBold));
        wrapper.addCell(headerCell);

        Table tabla = new Table(UnitValue.createPercentArray(new float[]{5, 32, 12, 12, 12, 27}));
        tabla.setWidth(UnitValue.createPercentValue(100));

        String[] encabezados = {"#", "Producto", "Unidad", "Cantidad", "Factor", "Equivalencia"};
        for (String encabezado : encabezados) {
            Cell enc = new Cell().setBackgroundColor(VERDE_FONDO).setPadding(4)
                    .setBorder(new SolidBorder(GRIS_BORDE, 0.4f));
            enc.add(new Paragraph(encabezado)
                    .setFont(helveticaBold).setFontSize(8).setFontColor(VERDE_PRINCIPAL).setMargin(0));
            tabla.addCell(enc);
        }

        int fila = 1;
        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            for (DetallePdfDto det : dto.getDetalles()) {
                tabla.addCell(crearCelda(String.valueOf(fila++), helvetica));
                tabla.addCell(crearCelda(det.getProducto(), helvetica));
                tabla.addCell(crearCelda(det.getUnidad() != null ? det.getUnidad() : "-", helvetica));
                tabla.addCell(crearCelda(det.getCantidad() != null
                        ? det.getCantidad().stripTrailingZeros().toPlainString() : "-", helvetica));
                tabla.addCell(crearCelda(det.getFactor() != null
                        ? det.getFactor().stripTrailingZeros().toPlainString() : "-", helvetica));
                tabla.addCell(crearCelda(det.getEquivalencia() != null ? det.getEquivalencia() : "-", helvetica));
            }
        } else {
            Cell vacio = new Cell(1, 6).setPadding(6).setBorder(new SolidBorder(GRIS_BORDE, 0.4f));
            vacio.add(new Paragraph("Este pedido no tiene productos registrados.")
                    .setFont(helvetica).setFontSize(8).setFontColor(GRIS_TEXTO).setMargin(0)
                    .setTextAlignment(TextAlignment.CENTER));
            tabla.addCell(vacio);
        }

        wrapper.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(4).add(tabla));

        // Resumen
        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            Cell resumenCell = new Cell().setBorder(Border.NO_BORDER).setPaddingTop(4);
            resumenCell.add(crearLineaDato("Total de productos:", String.valueOf(dto.getDetalles().size()),
                    helvetica, helveticaBold));
            wrapper.addCell(resumenCell);
        }

        document.add(wrapper);
    }

    private Cell crearCelda(String texto, PdfFont helvetica) {
        Cell celda = new Cell().setPadding(4).setBorder(new SolidBorder(GRIS_BORDE, 0.4f));
        celda.add(new Paragraph(texto != null ? texto : "-")
                .setFont(helvetica).setFontSize(8).setFontColor(NEGRO).setMargin(0));
        return celda;
    }

    // =========================================================================
    // SECCIÓN 3: OBSERVACIONES
    // =========================================================================
    private void crearObservaciones(Document document, PedidoPdfDto dto,
                                    PdfFont helvetica, PdfFont helveticaBold) {
        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{100}));
        wrapper.setWidth(ANCHO_CONTENIDO);
        wrapper.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        wrapper.setMarginBottom(10);

        Cell headerCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        headerCell.add(crearTituloSeccion("OBSERVACIONES", helveticaBold));
        wrapper.addCell(headerCell);

        Cell contentCell = new Cell().setBorder(Border.NO_BORDER).setPadding(8);
        String obs = (dto.getPedido() != null && dto.getPedido().getObservacion() != null
                && !dto.getPedido().getObservacion().isBlank())
                ? dto.getPedido().getObservacion()
                : "Ninguna observaci\u00f3n registrada.";
        contentCell.add(new Paragraph(obs)
                .setFont(helvetica).setFontSize(9).setFontColor(NEGRO).setMargin(0));
        wrapper.addCell(contentCell);
        document.add(wrapper);
    }

    // =========================================================================
    // SECCIÓN 4: FIRMAS
    // =========================================================================
    private void crearFirmas(Document document, PedidoPdfDto dto,
                             PdfFont helvetica, PdfFont helveticaBold) {
        Table firmasTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        firmasTable.setWidth(ANCHO_CONTENIDO);
        firmasTable.setMarginTop(10);

        // Columna izquierda: Elaborado por (supermercado)
        Cell empCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);
        empCell.add(new Paragraph("________________________________")
                .setFont(helvetica).setFontSize(10).setMargin(0));
        empCell.add(new Paragraph("Elaborado por")
                .setFont(helveticaBold).setFontSize(9).setFontColor(VERDE_PRINCIPAL).setMargin(0));
        String empresaNombre = dto.getEmpresa() != null ? dto.getEmpresa().getNombre() : "";
        empCell.add(new Paragraph(empresaNombre)
                .setFont(helvetica).setFontSize(8).setFontColor(GRIS_TEXTO).setMargin(0));
        if (dto.getPedido() != null && dto.getPedido().getUsuario() != null) {
            empCell.add(new Paragraph("Usuario: " + dto.getPedido().getUsuario())
                    .setFont(helvetica).setFontSize(7).setFontColor(GRIS_TEXTO).setMargin(0));
        }
        firmasTable.addCell(empCell);

        // Columna derecha: Recibido por (proveedor)
        Cell provCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);
        provCell.add(new Paragraph("________________________________")
                .setFont(helvetica).setFontSize(10).setMargin(0));
        provCell.add(new Paragraph("Recibido por")
                .setFont(helveticaBold).setFontSize(9).setFontColor(VERDE_PRINCIPAL).setMargin(0));
        provCell.add(new Paragraph(dto.getProveedor() != null ? dto.getProveedor().getNombre() : "")
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

    private Paragraph crearLineaEstado(String label, String estado,
                                       PdfFont helvetica, PdfFont helveticaBold) {
        Text labelText = new Text(label + " ")
                .setFont(helveticaBold).setFontSize(8).setFontColor(VERDE_SECUNDARIO);
        String valor = estado != null ? estado.replace('_', ' ') : "-";
        Color color = COLOR_ESTADOS.getOrDefault(estado != null ? estado.toUpperCase() : "", GRIS_TEXTO);
        Text valueText = new Text(valor)
                .setFont(helveticaBold).setFontSize(8).setFontColor(color);
        return new Paragraph().add(labelText).add(valueText)
                .setMargin(0).setMarginBottom(3).setFixedLeading(12);
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