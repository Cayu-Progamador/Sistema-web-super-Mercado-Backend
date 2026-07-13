package com.backendSupermercado.supermercasdo.shared.util;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoListadoDto;
import com.backendSupermercado.supermercasdo.modules.empleado.dto.EmpleadoResponseDto;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class ReporteEmpleadoUtil {

    private static final String TITULO_SISTEMA = "SUPERMERCADO LA MARTITA";
    private static final String SUBTITULO_SISTEMA = "Sistema de Gestión Comercial";
    private static final String TITULO_REPORTE = "REPORTE GENERAL DE EMPLEADOS";

    public static byte[] generarPdfDetalle(EmpleadoResponseDto empleado, String username) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            writer.setPageEvent(new PdfPageEventHelper() {
                private com.lowagie.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    ColumnText.showTextAligned(
                            writer.getDirectContent(),
                            Element.ALIGN_RIGHT,
                            new Phrase("Página " + writer.getCurrentPageNumber(), footerFont),
                            document.right(),
                            document.bottom() - 10,
                            0);
                }
            });

            Paragraph titulo = new Paragraph(TITULO_SISTEMA,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(4);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph(SUBTITULO_SISTEMA,
                    FontFactory.getFont(FontFactory.HELVETICA, 11));
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(6);
            document.add(subtitulo);

            Paragraph line = new Paragraph("__________________________________________________");
            line.setAlignment(Element.ALIGN_CENTER);
            line.setSpacingAfter(10);
            document.add(line);

            Paragraph tituloFicha = new Paragraph("FICHA DEL EMPLEADO",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            tituloFicha.setAlignment(Element.ALIGN_CENTER);
            tituloFicha.setSpacingAfter(16);
            document.add(tituloFicha);

            String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            document.add(new Paragraph("Fecha de generación : " + fechaStr,
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(new Paragraph("Generado por        : " + (username != null ? username : "Usuario"),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(new Paragraph(" "));

            Paragraph sep = new Paragraph(
                    "\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
            sep.setAlignment(Element.ALIGN_CENTER);
            sep.setSpacingAfter(10);
            document.add(sep);

            com.lowagie.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            com.lowagie.text.Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // DATOS PERSONALES
            Paragraph section1 = new Paragraph("DATOS PERSONALES", sectionFont);
            section1.setSpacingAfter(8);
            document.add(section1);

            document.add(new Paragraph("ID Empleado     : " + (empleado.getIdEmpleado() != null ? empleado.getIdEmpleado() : "\u2014"), valueFont));
            document.add(new Paragraph("Nombre Completo : " + (empleado.getNombreCompleto() != null ? empleado.getNombreCompleto() : "\u2014"), valueFont));
            document.add(new Paragraph("CI              : " + (empleado.getCi() != null ? empleado.getCi() : "\u2014"), valueFont));
            document.add(new Paragraph("Fecha Nacimiento: " + (empleado.getFechaNacimiento() != null ? empleado.getFechaNacimiento() : "\u2014"), valueFont));
            document.add(new Paragraph("Sexo            : " + (empleado.getSexo() != null ? empleado.getSexo() : "\u2014"), valueFont));

            document.add(new Paragraph(" "));

            // DATOS LABORALES
            Paragraph section2 = new Paragraph("DATOS LABORALES", sectionFont);
            section2.setSpacingAfter(8);
            document.add(section2);

            String estado = Boolean.TRUE.equals(empleado.getEstado()) ? "ACTIVO" : "INACTIVO";
            document.add(new Paragraph("Estado          : " + estado, valueFont));
            document.add(new Paragraph("Fecha Contrataci\u00f3n: " + (empleado.getFechaContratacion() != null ? empleado.getFechaContratacion() : "\u2014"), valueFont));

            document.add(new Paragraph(" "));

            // CONTACTO
            Paragraph section3 = new Paragraph("CONTACTO", sectionFont);
            section3.setSpacingAfter(8);
            document.add(section3);

            document.add(new Paragraph("Correo          : " + (empleado.getCorreo() != null ? empleado.getCorreo() : "\u2014"), valueFont));
            document.add(new Paragraph("Tel\u00e9fono        : " + (empleado.getTelefono() != null ? empleado.getTelefono() : "\u2014"), valueFont));

            document.add(new Paragraph(" "));

            // DIRECCIÓN
            Paragraph section4 = new Paragraph("DIRECCI\u00d3N", sectionFont);
            section4.setSpacingAfter(8);
            document.add(section4);

            document.add(new Paragraph("Pa\u00eds           : " + (empleado.getPais() != null ? empleado.getPais() : "\u2014"), valueFont));
            document.add(new Paragraph("Departamento    : " + (empleado.getDepartamento() != null ? empleado.getDepartamento() : "\u2014"), valueFont));
            document.add(new Paragraph("Ciudad          : " + (empleado.getCiudad() != null ? empleado.getCiudad() : "\u2014"), valueFont));
            document.add(new Paragraph("Zona / Barrio   : " + (empleado.getZona() != null ? empleado.getZona() : "\u2014"), valueFont));
            document.add(new Paragraph("Calle           : " + (empleado.getCalle() != null ? empleado.getCalle() : "\u2014"), valueFont));
            document.add(new Paragraph("N\u00famero         : " + (empleado.getNumero() != null ? empleado.getNumero() : "\u2014"), valueFont));
            document.add(new Paragraph("Referencia      : " + (empleado.getReferencia() != null ? empleado.getReferencia() : "\u2014"), valueFont));

            document.add(new Paragraph(" "));

            document.add(sep);

            document.add(new Paragraph(" "));
            Paragraph firmaLabel = new Paragraph("Firma Responsable", FontFactory.getFont(FontFactory.HELVETICA, 10));
            firmaLabel.setSpacingAfter(20);
            document.add(firmaLabel);

            Paragraph firmaLinea = new Paragraph("________________________", FontFactory.getFont(FontFactory.HELVETICA, 10));
            document.add(firmaLinea);

            document.add(new Paragraph(" "));
            Paragraph docFooter = new Paragraph(
                    "Documento generado autom\u00e1ticamente por el sistema.",
                    FontFactory.getFont(FontFactory.HELVETICA, 8));
            docFooter.setAlignment(Element.ALIGN_CENTER);
            docFooter.setSpacingBefore(4);
            document.add(docFooter);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF detalle de empleado", e);
        }

        return baos.toByteArray();
    }

    public static byte[] generarPdf(List<EmpleadoListadoDto> empleados, String username) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            final PdfTemplate totalTemplate = writer.getDirectContent().createTemplate(100, 12);

            writer.setPageEvent(new PdfPageEventHelper() {

                private com.lowagie.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    String texto = "Página " + writer.getCurrentPageNumber();
                    ColumnText.showTextAligned(
                            writer.getDirectContent(),
                            Element.ALIGN_RIGHT,
                            new Phrase(texto, footerFont),
                            document.right(),
                            document.bottom() - 10,
                            0);
                }

                @Override
                public void onCloseDocument(PdfWriter writer, Document document) {
                    String total = String.valueOf(writer.getCurrentPageNumber() - 1);
                    ColumnText.showTextAligned(
                            totalTemplate,
                            Element.ALIGN_LEFT,
                            new Phrase(total, footerFont),
                            0, 2, 0);
                }
            });

            Paragraph titulo = new Paragraph(TITULO_SISTEMA,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(4);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph(SUBTITULO_SISTEMA,
                    FontFactory.getFont(FontFactory.HELVETICA, 11));
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(6);
            document.add(subtitulo);

            Paragraph line = new Paragraph("__________________________________________________");
            line.setAlignment(Element.ALIGN_CENTER);
            line.setSpacingAfter(10);
            document.add(line);

            Paragraph tituloReporte = new Paragraph(TITULO_REPORTE,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            tituloReporte.setAlignment(Element.ALIGN_CENTER);
            tituloReporte.setSpacingAfter(16);
            document.add(tituloReporte);

            String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            document.add(new Paragraph("Fecha de generación: " + fechaStr,
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(new Paragraph("Generado por: " + (username != null ? username : "Usuario"),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 5f, 35f, 22f, 13f });

            com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            headerFont.setColor(255, 255, 255);

            String[] headers = { "#", "Nombre", "Teléfono", "Estado" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new java.awt.Color(41, 128, 185));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            com.lowagie.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            for (int i = 0; i < empleados.size(); i++) {
                EmpleadoListadoDto e = empleados.get(i);

                addCell(table, String.valueOf(i + 1), bodyFont, Element.ALIGN_CENTER);
                addCell(table, e.getNombreCompleto() != null ? e.getNombreCompleto() : "\u2014", bodyFont,
                        Element.ALIGN_LEFT);
                addCell(table, e.getTelefono() != null ? e.getTelefono() : "\u2014", bodyFont, Element.ALIGN_LEFT);
                addCell(table, Boolean.TRUE.equals(e.getEstado()) ? "Activo" : "Inactivo", bodyFont,
                        Element.ALIGN_CENTER);
            }

            document.add(table);

            document.add(new Paragraph(" "));
            Paragraph total = new Paragraph("Total de registros: " + empleados.size(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
            total.setSpacingBefore(10);
            document.add(total);

            Paragraph footerLine = new Paragraph(
                    "__________________________________________________");
            footerLine.setAlignment(Element.ALIGN_CENTER);
            footerLine.setSpacingBefore(8);
            document.add(footerLine);

            Paragraph docFooter = new Paragraph(
                    "Documento generado automáticamente por el Sistema de Gestión.",
                    FontFactory.getFont(FontFactory.HELVETICA, 8));
            docFooter.setAlignment(Element.ALIGN_CENTER);
            docFooter.setSpacingBefore(4);
            document.add(docFooter);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }

        return baos.toByteArray();
    }

    private static void addCell(PdfPTable table, String text, com.lowagie.text.Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        table.addCell(cell);
    }

    public static byte[] generarExcel(List<EmpleadoListadoDto> empleados) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Empleados");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setFont(getHeaderFont(workbook));
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle bodyStyle = workbook.createCellStyle();
            bodyStyle.setFont(getBodyFont(workbook));
            bodyStyle.setBorderTop(BorderStyle.THIN);
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            bodyStyle.setBorderLeft(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(bodyStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            String[] headers = { "#", "Nombre", "Teléfono", "Estado" };
            int[] colWidths = { 5, 40, 25, 10 };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, colWidths[i] * 256);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < empleados.size(); i++) {
                EmpleadoListadoDto e = empleados.get(i);
                Row row = sheet.createRow(i + 1);
                row.setHeightInPoints(18);

                Cell c0 = row.createCell(0);
                c0.setCellValue(i + 1);
                c0.setCellStyle(centerStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(e.getNombreCompleto() != null ? e.getNombreCompleto() : "\u2014");
                c1.setCellStyle(bodyStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(e.getTelefono() != null ? e.getTelefono() : "\u2014");
                c2.setCellStyle(bodyStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(Boolean.TRUE.equals(e.getEstado()) ? "Activo" : "Inactivo");
                c3.setCellStyle(centerStyle);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }

    private static Font getHeaderFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Calibri");
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        return font;
    }

    private static Font getBodyFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Calibri");
        return font;
    }
}
