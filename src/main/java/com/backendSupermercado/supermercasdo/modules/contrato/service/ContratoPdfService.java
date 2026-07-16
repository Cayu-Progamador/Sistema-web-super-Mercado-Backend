package com.backendSupermercado.supermercasdo.modules.contrato.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetallePdfDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetallePdfDto.ContratoPdfDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetallePdfDto.EmpleadoPdfDto;
import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoDetallePdfDto.EmpresaDto;
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
public class ContratoPdfService {

    private static final Color VERDE_PRINCIPAL = new DeviceRgb(27, 94, 32);
    private static final Color VERDE_SECUNDARIO = new DeviceRgb(46, 125, 50);
    //private static final Color GRIS_CLARO = new DeviceRgb(245, 245, 245);
    private static final Color GRIS_BORDE = new DeviceRgb(200, 200, 200);
    private static final Color GRIS_TEXTO = new DeviceRgb(100, 100, 100);
    private static final Color BLANCO = ColorConstants.WHITE;
    private static final Color NEGRO = ColorConstants.BLACK;
    private static final Color VERDE_BADGE = new DeviceRgb(76, 175, 80);
    private static final Color ROJO_BADGE = new DeviceRgb(244, 67, 54);

    private static final float MARGEN_IZQUIERDO = 25f;
    private static final float MARGEN_DERECHO = 25f;
    private static final float MARGEN_SUPERIOR = 25f;
    private static final float MARGEN_INFERIOR = 25f;
    private static final float ANCHO_CONTENIDO = PageSize.A4.getWidth() - MARGEN_IZQUIERDO - MARGEN_DERECHO;

    @Value("${app.uploads.directorio}")
    private String uploadsDir;

    public byte[] generarPdf(ContratoDetallePdfDto dto) {
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
            crearInformacionEmpleado(document, dto, helvetica, helveticaBold);
            crearInformacionContrato(document, dto, helvetica, helveticaBold);
            crearClausulas(document, dto, helvetica, helveticaBold);
            crearObservaciones(document, dto, helvetica, helveticaBold);
            crearFirmas(document, dto, helvetica, helveticaBold);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF del contrato", e);
        }

        return baos.toByteArray();
    }

    // =========================================================================
    // ENCABEZADO
    // =========================================================================
    private void crearEncabezado(Document document, ContratoDetallePdfDto dto,
                                  PdfFont helvetica, PdfFont helveticaBold) {
        EmpresaDto empresa = dto.getEmpresa();
        float fullWidth = ANCHO_CONTENIDO;

        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{25, 50, 25}));
        headerTable.setWidth(fullWidth);

        // Columna izquierda: Logo + Empresa
        Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        leftCell.add(crearLogoEmpresa(empresa, helveticaBold));
        headerTable.addCell(leftCell);

        // Columna central: Título
        Cell centerCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        Paragraph titulo = new Paragraph("DETALLE DE CONTRATO")
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
    // SECCIÓN 1: INFORMACIÓN DEL EMPLEADO
    // =========================================================================
    private void crearInformacionEmpleado(Document document, ContratoDetallePdfDto dto,
                                           PdfFont helvetica, PdfFont helveticaBold) {
        EmpleadoPdfDto emp = dto.getEmpleado();
        if (emp == null) return;

        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{100}));
        wrapper.setWidth(ANCHO_CONTENIDO);
        wrapper.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        wrapper.setMarginBottom(10);

        // Header de sección
        Cell headerCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        headerCell.add(crearTituloSeccion("INFORMACI\u00d3N DEL EMPLEADO", helveticaBold));
        wrapper.addCell(headerCell);

        // Contenido: 3 columnas
        Table content = new Table(UnitValue.createPercentArray(new float[]{20, 50, 30}));
        content.setWidth(UnitValue.createPercentValue(100));

        // Columna 1: Foto
        Cell fotoCell = new Cell().setBorder(Border.NO_BORDER).setPadding(5)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        Image foto = cargarImagen(emp.getFotoPath(), 80f, 100f);
        if (foto != null) {
            foto.setHorizontalAlignment(HorizontalAlignment.CENTER);
            fotoCell.add(foto);
        } else {
            Image defaultFoto = crearAvatarPorDefecto(40f);
            if (defaultFoto != null) {
                defaultFoto.setHorizontalAlignment(HorizontalAlignment.CENTER);
                fotoCell.add(defaultFoto);
            }
        }
        content.addCell(fotoCell);

        // Columna 2: Datos del empleado
        Cell dataCell = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
        dataCell.add(crearLineaDato("C\u00f3digo:", emp.getCodigo(), helvetica, helveticaBold));
        dataCell.add(crearLineaDato("Nombres:", emp.getNombres(), helvetica, helveticaBold));
        dataCell.add(crearLineaDato("Apellidos:", emp.getApellidos(), helvetica, helveticaBold));
        dataCell.add(crearLineaDato("C.I.:", emp.getDocumentoIdentidad(), helvetica, helveticaBold));
        dataCell.add(crearLineaDato("F. Nacimiento:", emp.getFechaNacimiento() != null
                ? emp.getFechaNacimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-", helvetica, helveticaBold));
        dataCell.add(crearLineaDato("Tel\u00e9fono:", emp.getTelefono(), helvetica, helveticaBold));
        dataCell.add(crearLineaDato("Correo:", emp.getCorreo(), helvetica, helveticaBold));
        dataCell.add(crearLineaDato("Cargo:", emp.getCargo(), helvetica, helveticaBold));
        dataCell.add(crearLineaDato("F. Ingreso:", emp.getFechaIngreso() != null
                ? emp.getFechaIngreso().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-", helvetica, helveticaBold));
        dataCell.add(crearLineaEstado("Estado:", emp.getEstadoActivo(), helvetica, helveticaBold));
        content.addCell(dataCell);

        // Columna 3: Empresa
        Cell empresaCell = new Cell().setBorder(Border.NO_BORDER).setPadding(5)
                .setVerticalAlignment(VerticalAlignment.TOP);
        empresaCell.add(crearInfoEmpresa(dto.getEmpresa(), helvetica, helveticaBold));
        content.addCell(empresaCell);

        wrapper.addCell(new Cell().setBorder(Border.NO_BORDER).add(content));
        document.add(wrapper);
    }

    // =========================================================================
    // SECCIÓN 2: INFORMACIÓN DEL CONTRATO
    // =========================================================================
    private void crearInformacionContrato(Document document, ContratoDetallePdfDto dto,
                                           PdfFont helvetica, PdfFont helveticaBold) {
        ContratoPdfDto c = dto.getContrato();
        if (c == null) return;

        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{100}));
        wrapper.setWidth(ANCHO_CONTENIDO);
        wrapper.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        wrapper.setMarginBottom(10);

        Cell headerCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        headerCell.add(crearTituloSeccion("INFORMACI\u00d3N DEL CONTRATO", helveticaBold));
        wrapper.addCell(headerCell);

        Table content = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        content.setWidth(UnitValue.createPercentValue(100));

        // Columna izquierda
        Cell leftCol = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
        leftCol.add(crearLineaDato("N\u00famero:", c.getNumeroContrato(), helvetica, helveticaBold));
        leftCol.add(crearLineaDato("Tipo:", c.getTipoContrato(), helvetica, helveticaBold));
        leftCol.add(crearLineaDato("Fecha Inicio:", c.getFechaInicio() != null
                ? c.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-", helvetica, helveticaBold));
        leftCol.add(crearLineaDato("Fecha Fin:", c.getFechaFin() != null
                ? c.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Indefinido", helvetica, helveticaBold));
        leftCol.add(crearLineaDato("Duraci\u00f3n:", c.getDuracion(), helvetica, helveticaBold));
        leftCol.add(crearLineaEstado("Estado:", c.getEstado(), helvetica, helveticaBold));
        if (c.getMotivo() != null && !c.getMotivo().isBlank()) {
            leftCol.add(crearLineaDato("Motivo:", c.getMotivo(), helvetica, helveticaBold));
        }
        content.addCell(leftCol);

        // Columna derecha
        Cell rightCol = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
        rightCol.add(crearLineaDato("Salario Mensual:", c.getSalarioMensual() != null
                ? "Bs. " + String.format("%,.2f", c.getSalarioMensual()) : "-", helvetica, helveticaBold));
        rightCol.add(crearLineaDato("Salario en Letras:", c.getSalarioLetras(), helvetica, helveticaBold));
        rightCol.add(crearLineaDato("Forma de Pago:", c.getFormaPago(), helvetica, helveticaBold));
        rightCol.add(crearLineaDato("Jornada:", c.getJornadaLaboral(), helvetica, helveticaBold));
        rightCol.add(crearLineaDato("Turno:", c.getTurno(), helvetica, helveticaBold));
        rightCol.add(crearLineaDato("Horario:", c.getHorario(), helvetica, helveticaBold));
        rightCol.add(crearLineaEstado("Control Asistencia:", c.getControlaAsistencia(), helvetica, helveticaBold));
        content.addCell(rightCol);

        wrapper.addCell(new Cell().setBorder(Border.NO_BORDER).add(content));
        document.add(wrapper);
    }

    // =========================================================================
    // SECCIÓN 3: CLÁUSULAS
    // =========================================================================
    private void crearClausulas(Document document, ContratoDetallePdfDto dto,
                                 PdfFont helvetica, PdfFont helveticaBold) {
        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{100}));
        wrapper.setWidth(ANCHO_CONTENIDO);
        wrapper.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        wrapper.setMarginBottom(10);

        Cell headerCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        headerCell.add(crearTituloSeccion("CL\u00c1USULAS PRINCIPALES", helveticaBold));
        wrapper.addCell(headerCell);

        Cell contentCell = new Cell().setBorder(Border.NO_BORDER).setPadding(8);
        if (dto.getClausulas() != null) {
            for (String clausula : dto.getClausulas()) {
                Paragraph item = new Paragraph("\u2022  " + clausula)
                        .setFont(helvetica).setFontSize(9).setFontColor(NEGRO)
                        .setMargin(0).setMarginBottom(4);
                contentCell.add(item);
            }
        }
        wrapper.addCell(contentCell);
        document.add(wrapper);
    }

    // =========================================================================
    // SECCIÓN 4: OBSERVACIONES
    // =========================================================================
    private void crearObservaciones(Document document, ContratoDetallePdfDto dto,
                                     PdfFont helvetica, PdfFont helveticaBold) {
        Table wrapper = new Table(UnitValue.createPercentArray(new float[]{100}));
        wrapper.setWidth(ANCHO_CONTENIDO);
        wrapper.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        wrapper.setMarginBottom(10);

        Cell headerCell = new Cell().setBorder(Border.NO_BORDER).setPadding(0);
        headerCell.add(crearTituloSeccion("OBSERVACIONES", helveticaBold));
        wrapper.addCell(headerCell);

        Cell contentCell = new Cell().setBorder(Border.NO_BORDER).setPadding(8);
        String obs = (dto.getObservaciones() != null && !dto.getObservaciones().isBlank())
                ? dto.getObservaciones()
                : "Ninguna observaci\u00f3n registrada.";
        contentCell.add(new Paragraph(obs)
                .setFont(helvetica).setFontSize(9).setFontColor(NEGRO).setMargin(0));
        wrapper.addCell(contentCell);
        document.add(wrapper);
    }

    // =========================================================================
    // SECCIÓN 5: FIRMAS
    // =========================================================================
    private void crearFirmas(Document document, ContratoDetallePdfDto dto,
                              PdfFont helvetica, PdfFont helveticaBold) {
        Table firmasTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        firmasTable.setWidth(ANCHO_CONTENIDO);
        firmasTable.setMarginTop(10);

        // Columna izquierda: Empleador
        Cell empCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);
        String empleadorNombre = dto.getEmpresa() != null ? dto.getEmpresa().getNombre() : "Empleador";

        Image firmaEmpImg = null; // No hay firma del empleador en BD por ahora
        if (firmaEmpImg != null) {
            firmaEmpImg.setMaxWidth(100);
            firmaEmpImg.setHorizontalAlignment(HorizontalAlignment.CENTER);
            empCell.add(firmaEmpImg);
        }

        empCell.add(new Paragraph("________________________________")
                .setFont(helvetica).setFontSize(10).setMargin(0));
        empCell.add(new Paragraph("Firma del Empleador")
                .setFont(helveticaBold).setFontSize(9).setFontColor(VERDE_PRINCIPAL).setMargin(0));
        empCell.add(new Paragraph(empleadorNombre)
                .setFont(helvetica).setFontSize(8).setFontColor(GRIS_TEXTO).setMargin(0));
        firmasTable.addCell(empCell);

        // Columna derecha: Empleado
        Cell emp2Cell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .setPadding(10);

        String nombreEmpleado = (dto.getEmpleado() != null
                ? (dto.getEmpleado().getNombres() != null ? dto.getEmpleado().getNombres() : "")
                + " " + (dto.getEmpleado().getApellidos() != null ? dto.getEmpleado().getApellidos() : "")
                : "Empleado").trim();

        emp2Cell.add(new Paragraph("________________________________")
                .setFont(helvetica).setFontSize(10).setMargin(0));
        emp2Cell.add(new Paragraph("Firma del Empleado")
                .setFont(helveticaBold).setFontSize(9).setFontColor(VERDE_PRINCIPAL).setMargin(0));
        emp2Cell.add(new Paragraph(nombreEmpleado)
                .setFont(helvetica).setFontSize(8).setFontColor(GRIS_TEXTO).setMargin(0));
        firmasTable.addCell(emp2Cell);

        document.add(firmasTable);
    }

    // =========================================================================
    // PIE DE PÁGINA
    // =========================================================================
    private void crearPiePagina(PdfDocument pdfDoc) {
        PdfCanvas canvas = new PdfCanvas(pdfDoc.getLastPage());
        Rectangle pageRect = pdfDoc.getDefaultPageSize();

        float y = MARGEN_INFERIOR - 5;
        float pageWidth = pageRect.getWidth();

        // Línea verde
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

            // Texto derecho: Página X de Y
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

    private Paragraph crearLineaEstado(String label, Boolean activo,
                                        PdfFont helvetica, PdfFont helveticaBold) {
        String valor = Boolean.TRUE.equals(activo) ? "ACTIVO" : "INACTIVO";
        return crearLineaDato(label, valor, helvetica, helveticaBold);
    }

    private Paragraph crearLineaEstado(String label, String estado,
                                        PdfFont helvetica, PdfFont helveticaBold) {
        String valor = estado != null ? estado.toUpperCase() : "-";
        return crearLineaDato(label, valor, helvetica, helveticaBold);
    }

    private Div crearInfoEmpresa(EmpresaDto empresa, PdfFont helvetica, PdfFont helveticaBold) {
        Div div = new Div();
        div.setBorder(new SolidBorder(GRIS_BORDE, 0.5f));
        div.setPadding(8);

        Image logo = cargarImagen(empresa.getLogoPath(), 30f, 30f);
        if (logo != null) {
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
            div.add(logo);
        }

        div.add(new Paragraph(empresa.getNombre())
                .setFont(helveticaBold).setFontSize(9).setFontColor(VERDE_PRINCIPAL)
                .setTextAlignment(TextAlignment.CENTER).setMargin(0));
        div.add(new Paragraph("NIT: " + (empresa.getNit() != null ? empresa.getNit() : "-"))
                .setFont(helvetica).setFontSize(7).setFontColor(GRIS_TEXTO)
                .setTextAlignment(TextAlignment.CENTER).setMargin(0));
        div.add(new Paragraph("Dir: " + (empresa.getDireccion() != null ? empresa.getDireccion() : "-"))
                .setFont(helvetica).setFontSize(7).setFontColor(GRIS_TEXTO)
                .setTextAlignment(TextAlignment.CENTER).setMargin(0));
        div.add(new Paragraph("Tel: " + (empresa.getTelefono() != null ? empresa.getTelefono() : "-"))
                .setFont(helvetica).setFontSize(7).setFontColor(GRIS_TEXTO)
                .setTextAlignment(TextAlignment.CENTER).setMargin(0));
        div.add(new Paragraph(empresa.getCiudad() != null ? empresa.getCiudad() : "")
                .setFont(helvetica).setFontSize(7).setFontColor(GRIS_TEXTO)
                .setTextAlignment(TextAlignment.CENTER).setMargin(0));

        return div;
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

    private Image crearAvatarPorDefecto(float size) {
        try {
            InputStream is = getClass().getResourceAsStream("/static/default-avatar.png");
            if (is != null) {
                byte[] bytes = is.readAllBytes();
                Image img = new Image(ImageDataFactory.create(bytes));
                img.scaleToFit(size, size);
                return img;
            }
        } catch (Exception e) {
            // No hay imagen por defecto
        }
        return null;
    }
}
