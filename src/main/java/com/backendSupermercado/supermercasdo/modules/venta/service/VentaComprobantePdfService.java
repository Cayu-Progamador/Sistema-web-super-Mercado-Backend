package com.backendSupermercado.supermercasdo.modules.venta.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.venta.entity.CuentaPorCobrar;
import com.backendSupermercado.supermercasdo.modules.venta.entity.DetalleVenta;
import com.backendSupermercado.supermercasdo.modules.venta.entity.PagoVenta;
import com.backendSupermercado.supermercasdo.modules.venta.entity.Venta;
import com.backendSupermercado.supermercasdo.modules.venta.enums.MetodoPago;
import com.backendSupermercado.supermercasdo.modules.configuracion.entity.ConfiguracionEmpresa;
import com.backendSupermercado.supermercasdo.modules.configuracion.repository.ConfiguracionEmpresaRepository;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.DottedBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class VentaComprobantePdfService {

    private final ConfiguracionEmpresaRepository configuracionEmpresaRepository;

    public VentaComprobantePdfService(ConfiguracionEmpresaRepository configuracionEmpresaRepository) {
        this.configuracionEmpresaRepository = configuracionEmpresaRepository;
    }

    private static final float ANCHO_TICKET = 226.77f; // 80 mm en puntos
    // El comprobante se imprime en ticket térmico monocromático.
    private static final Color VERDE = ColorConstants.BLACK;
    private static final Color VERDE_CLARO = ColorConstants.WHITE;
    private static final Color GRIS = ColorConstants.BLACK;
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(Venta venta) {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        float altoEstimado = 470f + (venta.getDetalles().size() * 42f);

        try (PdfWriter writer = new PdfWriter(salida);
             PdfDocument pdf = new PdfDocument(writer);
             Document documento = new Document(pdf, new PageSize(ANCHO_TICKET, altoEstimado))) {

            documento.setMargins(18, 14, 18, 14);
            PdfFont normal = PdfFontFactory.createFont("Helvetica", "WinAnsi");
            PdfFont negrita = PdfFontFactory.createFont("Helvetica-Bold", "WinAnsi");

            titulo(documento, venta, normal, negrita);
            datosVenta(documento, venta, normal, negrita);
            detalle(documento, venta, normal, negrita);
            resumen(documento, venta, normal, negrita);
            pago(documento, venta, normal, negrita);
            verificacion(documento, venta, normal, negrita, pdf);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el comprobante PDF", ex);
        }
        return salida.toByteArray();
    }

    private void titulo(Document doc, Venta venta, PdfFont normal, PdfFont negrita) {
        ConfiguracionEmpresa empresa = configuracionEmpresaRepository.findFirstByOrderByIdAsc().orElse(null);
        String nombre = valor(empresa == null ? null : empresa.getNombreComercial(), "NOMBRE DEL SUPERMERCADO");
        String razon = valor(empresa == null ? null : empresa.getRazonSocial(), "COMPROBANTE DE VENTA");
        String direccion = valor(empresa == null ? null : empresa.getDireccion(), "AV. PRINCIPAL #100") + ", " + valor(empresa == null ? null : empresa.getDepartamento(), "LA PAZ - BOLIVIA");
        String datos = direccion + "\nNIT: " + valor(empresa == null ? null : empresa.getNit(), "000000000") + "\nTelefono: " + valor(empresa == null ? null : empresa.getTelefono(), "70000000");
        if (empresa != null && empresa.getLogoPath() != null && Files.exists(Path.of(empresa.getLogoPath()))) {
            try {
                doc.add(new Image(ImageDataFactory.create(empresa.getLogoPath())).setWidth(42).setHeight(42).setAutoScale(false).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER));
            } catch (Exception ignored) {
                // El comprobante continúa generándose aunque el logo no esté disponible.
            }
        }
        doc.add(new Paragraph(nombre).setFont(negrita).setFontSize(9)
                .setFontColor(VERDE).setTextAlignment(TextAlignment.CENTER).setMargin(0));
        doc.add(new Paragraph(razon).setFont(negrita).setFontSize(8)
                .setFontColor(GRIS).setTextAlignment(TextAlignment.CENTER).setMargin(0));
        doc.add(new Paragraph(datos)
                .setFont(normal).setFontSize(6.5f).setFontColor(GRIS)
                .setTextAlignment(TextAlignment.CENTER).setMarginBottom(3));
        doc.add(new Paragraph("✓  VENTA " + venta.getEstado() + "     " + texto(venta.getCodigo()))
                .setFont(negrita).setFontSize(8).setFontColor(VERDE)
                .setBackgroundColor(VERDE_CLARO).setPadding(3).setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(4));
    }

    private void datosVenta(Document doc, Venta venta, PdfFont normal, PdfFont negrita) {
        Table tabla = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }))
                .useAllAvailableWidth().setBorder(Border.NO_BORDER);
        agregarDato(tabla, "Fecha y hora", venta.getFechaVenta() == null ? "--" : venta.getFechaVenta().format(FECHA), normal, negrita);
        agregarDato(tabla, "Caja", venta.getIdCaja() == null ? "Caja 01" : "Caja " + venta.getIdCaja(), normal, negrita);
        agregarDato(tabla, "Vendedor", nombreVendedor(venta), normal, negrita);
        agregarDato(tabla, "Cliente", venta.getCliente() == null ? "Consumidor final" : nombreCliente(venta), normal, negrita);
        if (venta.getCliente() != null) {
            agregarDato(tabla, "CI", texto(venta.getCliente().getCi()), normal, negrita);
            agregarDato(tabla, "Celular", texto(venta.getCliente().getTelefono()), normal, negrita);
        }
        doc.add(tabla.setMarginBottom(7));
    }

    private void detalle(Document doc, Venta venta, PdfFont normal, PdfFont negrita) {
        Table tabla = new Table(UnitValue.createPercentArray(new float[] { 72, 30, 26, 48, 52 }))
                .useAllAvailableWidth().setBorder(Border.NO_BORDER)
                .setBorderTop(new DottedBorder(ColorConstants.BLACK, .5f))
                .setBorderBottom(new DottedBorder(ColorConstants.BLACK, .5f));
        tabla.addHeaderCell(celda("Producto", negrita, TextAlignment.LEFT, true));
        tabla.addHeaderCell(celda("Unidad", negrita, TextAlignment.RIGHT, true));
        tabla.addHeaderCell(celda("Cant.", negrita, TextAlignment.RIGHT, true));
        tabla.addHeaderCell(celda("P. unit.", negrita, TextAlignment.RIGHT, true));
        tabla.addHeaderCell(celda("Importe", negrita, TextAlignment.RIGHT, true));
        for (DetalleVenta item : venta.getDetalles()) {
            String producto = item.getProducto() == null ? "Producto" : item.getProducto().getNombre();
            String unidad = item.getPresentacion() == null || item.getPresentacion().getUnidadMedida() == null
                    ? "--" : item.getPresentacion().getUnidadMedida().getNombre();
            tabla.addCell(celda(producto, normal, TextAlignment.LEFT, false));
            tabla.addCell(celda(unidad, normal, TextAlignment.RIGHT, false));
            tabla.addCell(celda(numero(item.getCantidad()), normal, TextAlignment.RIGHT, false));
            tabla.addCell(celda("Bs " + dinero(item.getPrecioUnitario()), normal, TextAlignment.RIGHT, false));
            tabla.addCell(celda("Bs " + dinero(item.getImporte()), negrita, TextAlignment.RIGHT, false));
        }
        doc.add(tabla.setMarginBottom(7));
    }

    private void resumen(Document doc, Venta venta, PdfFont normal, PdfFont negrita) {
        doc.add(fila("Subtotal", dinero(venta.getSubtotal()), normal, negrita));
        doc.add(fila("Descuento", dinero(venta.getDescuento()), normal, negrita));
        doc.add(filaTotal("Total", "Bs " + dinero(venta.getTotal()), negrita));
    }

    private void pago(Document doc, Venta venta, PdfFont normal, PdfFont negrita) {
        PagoVenta ultimo = venta.getPagos().isEmpty() ? null : venta.getPagos().get(venta.getPagos().size() - 1);
        CuentaPorCobrar cuenta = venta.getCuentaPorCobrar();
        if (cuenta != null) {
            doc.add(fila("Método", "CRÉDITO", normal, negrita));
            doc.add(fila("Pago inicial", dinero(cuenta.getPagoInicial()), normal, negrita));
            doc.add(fila("Saldo pendiente", dinero(cuenta.getSaldoPendiente()), normal, negrita));
            doc.add(fila("Estado", texto(cuenta.getEstado()), normal, negrita));
            doc.add(fila("Fecha límite", String.valueOf(cuenta.getFechaLimite()), normal, negrita));
        } else if (ultimo != null) {
            doc.add(fila("Método", texto(ultimo.getMetodoPago()), normal, negrita));
            if (ultimo.getMetodoPago() == MetodoPago.MIXTO) {
                doc.add(fila("Efectivo recibido", dinero(ultimo.getMontoEfectivo()), normal, negrita));
                doc.add(fila("Pago por QR", dinero(ultimo.getMontoQr()), normal, negrita));
                doc.add(fila("Total recibido", dinero(ultimo.getTotalRecibido()), normal, negrita));
            } else if (ultimo.getMetodoPago() == MetodoPago.QR) {
                doc.add(fila("Pago por QR", dinero(ultimo.getMontoQr()), normal, negrita));
            } else {
                doc.add(fila("Monto recibido", dinero(ultimo.getTotalRecibido()), normal, negrita));
            }
            if (ultimo.getReferenciaQr() != null && !ultimo.getReferenciaQr().isBlank()) {
                doc.add(fila("Referencia QR", ultimo.getReferenciaQr(), normal, negrita));
            }
            doc.add(fila("Cambio a entregar", dinero(ultimo.getCambio()), normal, negrita));
        }
    }

    private void verificacion(Document doc, Venta venta, PdfFont normal, PdfFont negrita, PdfDocument pdf) {
        BarcodeQRCode codigoQr = new BarcodeQRCode(venta.getCodigo());
        Image imagen = new Image(codigoQr.createFormXObject(ColorConstants.BLACK, pdf)).setWidth(58).setHeight(58);
        Table tabla = new Table(UnitValue.createPercentArray(new float[] { 70, 120 })).useAllAvailableWidth()
                .setBorderTop(new DottedBorder(ColorConstants.BLACK, .5f)).setMarginTop(9).setPaddingTop(7);
        tabla.addCell(new Cell().setBorder(Border.NO_BORDER).add(imagen));
        tabla.addCell(new Cell().setBorder(Border.NO_BORDER).add(new Paragraph("Verificación del comprobante")
                .setFont(negrita).setFontSize(7).setFontColor(GRIS))
                .add(new Paragraph("Código: " + venta.getCodigo()).setFont(normal).setFontSize(7).setFontColor(VERDE)));
        doc.add(tabla);
        ConfiguracionEmpresa empresa = configuracionEmpresaRepository.findFirstByOrderByIdAsc().orElse(null);
        doc.add(new Paragraph(valor(empresa == null ? null : empresa.getMensajeComprobante(), "Gracias por su compra. Conserve este comprobante."))
                .setFont(normal).setFontSize(7).setFontColor(GRIS).setTextAlignment(TextAlignment.CENTER).setMarginTop(7));
    }

    private String valor(String valor, String defecto) {
        return valor == null || valor.isBlank() ? defecto : valor;
    }

    private void agregarDato(Table tabla, String etiqueta, String valor, PdfFont normal, PdfFont negrita) {
        tabla.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(2)
                .add(new Paragraph(etiqueta).setFont(normal).setFontSize(6).setFontColor(GRIS).setMargin(0)));
        tabla.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(2)
                .add(new Paragraph(valor).setFont(negrita).setFontSize(6.5f).setTextAlignment(TextAlignment.RIGHT).setMargin(0)));
    }

    private Table fila(String etiqueta, String valor, PdfFont normal, PdfFont negrita) {
        Table tabla = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }))
                .useAllAvailableWidth().setBorder(Border.NO_BORDER).setMargin(0);
        tabla.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(2)
                .add(new Paragraph(etiqueta).setFont(normal).setFontSize(7).setFontColor(GRIS).setMargin(0)));
        tabla.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(2)
                .add(new Paragraph(valor).setFont(negrita).setFontSize(7)
                        .setFontColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.RIGHT).setMargin(0)));
        return tabla;
    }

    private Table filaTotal(String etiqueta, String valor, PdfFont negrita) {
        return fila(etiqueta, valor, negrita, negrita)
                .setBorderTop(new DottedBorder(ColorConstants.BLACK, .8f)).setPaddingTop(6);
    }

    private Cell celda(String texto, PdfFont fuente, TextAlignment alineacion, boolean encabezado) {
        Cell celda = new Cell().setPadding(3).setBorder(Border.NO_BORDER);
        if (encabezado) celda.setBackgroundColor(VERDE_CLARO);
        return celda.add(new Paragraph(texto).setFont(fuente).setFontSize(encabezado ? 6.5f : 6.3f)
                .setTextAlignment(alineacion).setMargin(0));
    }

    private String nombreCliente(Venta venta) {
        var cliente = venta.getCliente();
        return String.join(" ", java.util.stream.Stream.of(cliente.getNombres(), cliente.getApellidoPaterno(), cliente.getApellidoMaterno())
                .filter(v -> v != null && !v.isBlank()).toList());
    }

    private String nombreVendedor(Venta venta) {
        if (venta.getUsuario() == null) return "--";
        if (venta.getUsuario().getEmpleado() != null && venta.getUsuario().getEmpleado().getPersona() != null) {
            var persona = venta.getUsuario().getEmpleado().getPersona();
            String nombre = String.join(" ", java.util.stream.Stream.of(
                    persona.getNombres(), persona.getApellidoPaterno(), persona.getApellidoMaterno())
                    .filter(v -> v != null && !v.isBlank()).toList());
            if (!nombre.isBlank()) return nombre;
        }
        return venta.getUsuario().getUsername();
    }

    private String dinero(BigDecimal valor) { return valor == null ? "0.00" : valor.setScale(2).toPlainString(); }
    private String numero(BigDecimal valor) { return valor == null ? "0" : valor.stripTrailingZeros().toPlainString(); }
    private String texto(Object valor) { return valor == null ? "--" : valor.toString(); }
}
