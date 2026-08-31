package com.backendSupermercado.supermercasdo.modules.almacen.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.almacen.entity.Almacen;
import com.backendSupermercado.supermercasdo.modules.empleado.entity.Empleado;
import com.backendSupermercado.supermercasdo.modules.inventario.entity.Existencia;
import com.backendSupermercado.supermercasdo.modules.productos.entity.PresentacionProducto;
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

@Service
public class AlmacenInventarioPdfService {

    private static final Color VERDE = new DeviceRgb(27, 94, 32);
    private static final Color VERDE_SECUNDARIO = new DeviceRgb(46, 125, 50);
    private static final Color VERDE_FONDO = new DeviceRgb(232, 245, 233);
    private static final Color GRIS = new DeviceRgb(100, 100, 100);
    private static final Color GRIS_BORDE = new DeviceRgb(205, 215, 202);
    private static final Color ROJO = new DeviceRgb(198, 40, 40);
    private static final Color ROJO_FONDO = new DeviceRgb(255, 235, 238);
    private static final Color ROJO_VENCIDO = new DeviceRgb(183, 28, 28);
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Value("${app.empresa.nombre:Supermercado}")
    private String empresaNombre;

    @Value("${app.empresa.nit:}")
    private String empresaNit;

    public byte[] generar(Almacen almacen, List<Existencia> existencias, String generadoPor) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(salida);
             PdfDocument pdf = new PdfDocument(writer);
             Document documento = new Document(pdf, PageSize.A4.rotate())) {

            PdfFont normal = PdfFontFactory.createFont("Helvetica", "WinAnsi");
            PdfFont negrita = PdfFontFactory.createFont("Helvetica-Bold", "WinAnsi");
            documento.setMargins(28, 25, 32, 25);
            agregarPiePagina(pdf, normal);

            agregarEncabezado(documento, almacen, generadoPor, normal, negrita);
            agregarInformacion(documento, almacen, existencias, normal, negrita);
            agregarTabla(documento, existencias, normal, negrita);
            agregarLeyenda(documento, normal, negrita);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el PDF de inventario del almacén", ex);
        }
        return salida.toByteArray();
    }

    private void agregarEncabezado(Document documento, Almacen almacen, String generadoPor,
            PdfFont normal, PdfFont negrita) {
        Table encabezado = new Table(UnitValue.createPercentArray(new float[]{28, 44, 28})).useAllAvailableWidth();
        encabezado.addCell(sinBorde(new Paragraph(empresaNombre).setFont(negrita).setFontSize(12).setFontColor(VERDE))
                .add(new Paragraph(empresaNit == null || empresaNit.isBlank() ? "" : "NIT: " + empresaNit)
                        .setFont(normal).setFontSize(8).setFontColor(GRIS)));
        encabezado.addCell(sinBorde(new Paragraph("INVENTARIO POR ALMACÉN").setFont(negrita).setFontSize(18)
                .setFontColor(VERDE).setTextAlignment(TextAlignment.CENTER)));
        encabezado.addCell(sinBorde(new Paragraph("Generado: " + LocalDateTime.now().format(FECHA_HORA))
                .setFont(normal).setFontSize(8).setFontColor(GRIS).setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("Usuario: " + texto(generadoPor)).setFont(normal).setFontSize(8)
                        .setFontColor(GRIS).setTextAlignment(TextAlignment.RIGHT)));
        documento.add(encabezado);
        documento.add(new Paragraph(almacen.getNombre() + "  ·  " + almacen.getCodigo())
                .setFont(negrita).setFontSize(12).setFontColor(VERDE_SECUNDARIO)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(12));
    }

    private void agregarInformacion(Document documento, Almacen almacen, List<Existencia> existencias,
            PdfFont normal, PdfFont negrita) {
        long productos = existencias.stream().map(e -> e.getProducto().getIdProducto()).distinct().count();
        long ubicaciones = existencias.stream().map(e -> e.getUbicacion().getIdUbicacion()).distinct().count();
        BigDecimal stock = existencias.stream().map(Existencia::getCantidad).reduce(BigDecimal.ZERO, BigDecimal::add);
        long proximos = existencias.stream().filter(e -> "PRÓXIMO A VENCER".equals(estadoVencimiento(e))).count();
        long vencidos = existencias.stream().filter(e -> "VENCIDO".equals(estadoVencimiento(e))).count();

        Table caja = new Table(UnitValue.createPercentArray(new float[]{35, 35, 15, 15})).useAllAvailableWidth();
        caja.setBorder(new SolidBorder(GRIS_BORDE, .6f)).setMarginBottom(12);
        caja.addCell(celdaInfo("Tipo", almacen.getTipoAlmacen().getNombre(), normal, negrita));
        caja.addCell(celdaInfo("Responsable / teléfono", responsableConTelefono(almacen.getResponsable()), normal, negrita));
        caja.addCell(celdaInfo("Ubicaciones", String.valueOf(ubicaciones), normal, negrita));
        caja.addCell(celdaInfo("Productos", String.valueOf(productos), normal, negrita));
        caja.addCell(celdaInfo("Dirección / referencia", texto(almacen.getDireccion()), normal, negrita));
        caja.addCell(celdaInfo("Stock total", numero(stock), normal, negrita));
        caja.addCell(celdaAlerta("Por vencer", String.valueOf(proximos), proximos > 0, normal, negrita));
        caja.addCell(celdaAlerta("Vencidos", String.valueOf(vencidos), vencidos > 0, normal, negrita));
        documento.add(caja);
    }

    private void agregarTabla(Document documento, List<Existencia> existencias,
            PdfFont normal, PdfFont negrita) {
        documento.add(new Paragraph("DETALLE DE PRODUCTOS").setFont(negrita).setFontSize(10)
                .setFontColor(ColorConstants.WHITE).setBackgroundColor(VERDE).setPadding(6).setMarginBottom(0));
        Table tabla = new Table(UnitValue.createPercentArray(
                new float[]{3, 8, 17, 9, 9, 10, 8, 9, 8, 7, 8, 8})).useAllAvailableWidth();
        String[] titulos = {"#", "Código", "Producto", "Categoría", "Marca", "Presentación",
                "Lote", "Vencimiento", "Cantidad", "Unidad", "Ubicación", "Estado"};
        for (String titulo : titulos) tabla.addHeaderCell(celdaCabecera(titulo, negrita));

        int numeroFila = 1;
        for (Existencia existencia : existencias) {
            String estado = estadoVencimiento(existencia);
            Color fondo = "VENCIDO".equals(estado) ? ROJO_VENCIDO
                    : "PRÓXIMO A VENCER".equals(estado) ? ROJO_FONDO : ColorConstants.WHITE;
            Color texto = "VENCIDO".equals(estado) ? ColorConstants.WHITE
                    : "PRÓXIMO A VENCER".equals(estado) ? ROJO : ColorConstants.BLACK;
            var producto = existencia.getProducto();
            agregarFila(tabla, normal, fondo, texto,
                    String.valueOf(numeroFila++), texto(producto.getCodigoInterno()), producto.getNombre(),
                    producto.getSubcategoria() != null && producto.getSubcategoria().getCategoria() != null
                            ? producto.getSubcategoria().getCategoria().getNombre() : "-",
                    producto.getMarca() != null ? producto.getMarca().getNombre() : "-",
                    presentacionPrincipal(producto.getPresentaciones()),
                    existencia.getLote() != null ? existencia.getLote().getCodigoLote() : "-",
                    existencia.getLote() != null && existencia.getLote().getFechaVencimiento() != null
                            ? existencia.getLote().getFechaVencimiento().format(FECHA) : "-",
                    numero(existencia.getCantidad()),
                    producto.getUnidadBase() != null ? producto.getUnidadBase().getAbreviatura() : "-",
                    existencia.getUbicacion().getCodigo() + " - " + existencia.getUbicacion().getNombre(), estado);
        }
        if (existencias.isEmpty()) {
            tabla.addCell(new Cell(1, titulos.length).setPadding(10).setBorder(new SolidBorder(GRIS_BORDE, .4f))
                    .add(new Paragraph("El almacén no tiene existencias disponibles.").setFont(normal)
                            .setFontSize(8).setFontColor(GRIS).setTextAlignment(TextAlignment.CENTER)));
        }
        documento.add(tabla);
    }

    private void agregarLeyenda(Document documento, PdfFont normal, PdfFont negrita) {
        documento.add(new Paragraph("Estados de vencimiento: ").setFont(negrita).setFontSize(8).setFontColor(VERDE)
                .add("VIGENTE: más de 30 días · PRÓXIMO A VENCER: 30 días o menos · VENCIDO: fecha superada · SIN VENCIMIENTO: no aplica")
                .setFont(normal).setFontSize(8).setFontColor(GRIS).setMarginTop(8));
    }

    private void agregarPiePagina(PdfDocument pdf, PdfFont fuente) {
        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, evento -> {
            PdfDocumentEvent pagina = (PdfDocumentEvent) evento;
            PdfCanvas canvas = new PdfCanvas(pagina.getPage());
            canvas.beginText().setFontAndSize(fuente, 7).setFillColor(GRIS)
                    .moveText(25, 16).showText(empresaNombre + " · Inventario de almacén")
                    .moveText(PageSize.A4.rotate().getWidth() - 210, 0)
                    .showText("Página " + pagina.getDocument().getPageNumber(pagina.getPage())).endText();
            canvas.release();
        });
    }

    private Cell celdaCabecera(String texto, PdfFont fuente) {
        return new Cell().setBackgroundColor(VERDE_FONDO).setPadding(4)
                .setBorder(new SolidBorder(GRIS_BORDE, .4f))
                .add(new Paragraph(texto).setFont(fuente).setFontSize(6.7f).setFontColor(VERDE)
                        .setTextAlignment(TextAlignment.CENTER).setMargin(0));
    }

    private void agregarFila(Table tabla, PdfFont fuente, Color fondo, Color colorTexto, String... valores) {
        for (String valor : valores) {
            tabla.addCell(new Cell().setBackgroundColor(fondo).setPadding(3)
                    .setBorder(new SolidBorder(GRIS_BORDE, .35f))
                    .add(new Paragraph(texto(valor)).setFont(fuente).setFontSize(6.5f)
                            .setFontColor(colorTexto).setMargin(0)));
        }
    }

    private Cell celdaInfo(String etiqueta, String valor, PdfFont normal, PdfFont negrita) {
        return new Cell().setBorder(Border.NO_BORDER).setPadding(6)
                .add(new Paragraph(etiqueta).setFont(normal).setFontSize(7).setFontColor(GRIS).setMargin(0))
                .add(new Paragraph(texto(valor)).setFont(negrita).setFontSize(8).setFontColor(VERDE).setMargin(0));
    }

    private Cell celdaAlerta(String etiqueta, String valor, boolean alerta, PdfFont normal, PdfFont negrita) {
        Cell celda = celdaInfo(etiqueta, valor, normal, negrita);
        if (alerta) celda.setBackgroundColor(ROJO_FONDO);
        return celda;
    }

    private Cell sinBorde(Paragraph contenido) {
        return new Cell().setBorder(Border.NO_BORDER).setPadding(2).add(contenido);
    }

    private String estadoVencimiento(Existencia existencia) {
        if (existencia.getLote() == null || existencia.getLote().getFechaVencimiento() == null) return "SIN VENCIMIENTO";
        LocalDate fecha = existencia.getLote().getFechaVencimiento();
        LocalDate hoy = LocalDate.now();
        if (fecha.isBefore(hoy)) return "VENCIDO";
        if (!fecha.isAfter(hoy.plusDays(30))) return "PRÓXIMO A VENCER";
        return "VIGENTE";
    }

    private String presentacionPrincipal(List<PresentacionProducto> presentaciones) {
        if (presentaciones == null) return "-";
        return presentaciones.stream().filter(p -> Boolean.TRUE.equals(p.getEsPrincipal()))
                .map(PresentacionProducto::getNombrePresentacion).findFirst().orElse("-");
    }

    private String nombreResponsable(Empleado empleado) {
        if (empleado == null || empleado.getPersona() == null) return "Sin responsable";
        var persona = empleado.getPersona();
        return java.util.stream.Stream.of(persona.getNombres(), persona.getApellidoPaterno(), persona.getApellidoMaterno())
                .filter(valor -> valor != null && !valor.isBlank()).reduce((a, b) -> a + " " + b).orElse("Sin responsable");
    }

    private String responsableConTelefono(Empleado empleado) {
        String nombre = nombreResponsable(empleado);
        if (empleado == null || empleado.getPersona() == null
                || empleado.getPersona().getContactos() == null) return nombre;
        String telefono = empleado.getPersona().getContactos().stream()
                .map(contacto -> contacto.getTelefono())
                .filter(valor -> valor != null && !valor.isBlank())
                .findFirst().orElse(null);
        return telefono == null ? nombre : nombre + " · " + telefono;
    }

    private String numero(BigDecimal valor) {
        return valor == null ? "0" : valor.stripTrailingZeros().toPlainString();
    }

    private String texto(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }
}
