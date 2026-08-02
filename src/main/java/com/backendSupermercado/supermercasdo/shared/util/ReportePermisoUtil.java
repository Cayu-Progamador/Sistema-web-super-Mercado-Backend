package com.backendSupermercado.supermercasdo.shared.util;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.backendSupermercado.supermercasdo.modules.permiso_personal.dto.SolicitudPermisoResponseDto;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class ReportePermisoUtil {

    private static final String TITULO_SISTEMA = "SUPERMERCADO LA MARTITA";
    private static final String SUBTITULO_SISTEMA = "Sistema de Gesti\u00f3n Comercial";
    private static final String TITULO_REPORTE = "REPORTE DE SOLICITUDES DE PERMISO";

    public static byte[] generarPdf(List<SolicitudPermisoResponseDto> solicitudes, String username) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    String texto = "P\u00e1gina " + writer.getCurrentPageNumber();
                    com.lowagie.text.pdf.ColumnText.showTextAligned(
                            writer.getDirectContent(),
                            Element.ALIGN_RIGHT,
                            new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 8)),
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

            Paragraph tituloReporte = new Paragraph(TITULO_REPORTE,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            tituloReporte.setAlignment(Element.ALIGN_CENTER);
            tituloReporte.setSpacingAfter(16);
            document.add(tituloReporte);

            String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            document.add(new Paragraph("Fecha de generaci\u00f3n: " + fechaStr,
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(new Paragraph("Generado por: " + (username != null ? username : "Usuario"),
                    FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 3f, 18f, 14f, 16f, 12f, 12f, 12f });

            com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            headerFont.setColor(255, 255, 255);

            String[] headers = { "#", "Empleado", "Cargo", "Tipo Permiso", "Inicio", "Fin", "Estado" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new java.awt.Color(46, 125, 50));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            com.lowagie.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            for (int i = 0; i < solicitudes.size(); i++) {
                SolicitudPermisoResponseDto s = solicitudes.get(i);
                addCell(table, String.valueOf(i + 1), bodyFont, Element.ALIGN_CENTER);
                addCell(table, s.getNombreEmpleado() != null ? s.getNombreEmpleado() : "\u2014", bodyFont, Element.ALIGN_LEFT);
                addCell(table, s.getNombreCargo() != null ? s.getNombreCargo() : "\u2014", bodyFont, Element.ALIGN_LEFT);
                addCell(table, s.getNombreTipo() != null ? s.getNombreTipo() : "\u2014", bodyFont, Element.ALIGN_LEFT);
                addCell(table, s.getFechaInicio() != null ? s.getFechaInicio().toString() : "\u2014", bodyFont, Element.ALIGN_CENTER);
                addCell(table, s.getFechaFin() != null ? s.getFechaFin().toString() : "\u2014", bodyFont, Element.ALIGN_CENTER);
                addCell(table, s.getNombreEstado() != null ? s.getNombreEstado() : "\u2014", bodyFont, Element.ALIGN_CENTER);
            }

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de solicitudes de permiso", e);
        }
    }

    public static byte[] generarExcel(List<SolicitudPermisoResponseDto> solicitudes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Workbook workbook = new XSSFWorkbook();
            try {
                Sheet sheet = workbook.createSheet("Permisos");

                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                Font headerFont = workbook.createFont();
                headerFont.setFontHeightInPoints((short) 10);
                headerFont.setFontName("Calibri");
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);

                CellStyle bodyStyle = workbook.createCellStyle();
                bodyStyle.setFont(workbook.createFont());
                bodyStyle.setBorderTop(BorderStyle.THIN);
                bodyStyle.setBorderBottom(BorderStyle.THIN);
                bodyStyle.setBorderLeft(BorderStyle.THIN);
                bodyStyle.setBorderRight(BorderStyle.THIN);

                CellStyle centerStyle = workbook.createCellStyle();
                centerStyle.cloneStyleFrom(bodyStyle);
                centerStyle.setAlignment(HorizontalAlignment.CENTER);

                String[] headers = { "#", "Empleado", "Cargo", "Tipo Permiso", "Inicio", "Fin", "Estado" };
                int[] colWidths = { 5, 35, 25, 22, 15, 15, 15 };

                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    sheet.setColumnWidth(i, colWidths[i] * 256);
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                for (int i = 0; i < solicitudes.size(); i++) {
                    SolicitudPermisoResponseDto s = solicitudes.get(i);
                    Row row = sheet.createRow(i + 1);
                    row.setHeightInPoints(18);

                    Cell c0 = row.createCell(0);
                    c0.setCellValue(i + 1);
                    c0.setCellStyle(centerStyle);

                    Cell c1 = row.createCell(1);
                    c1.setCellValue(s.getNombreEmpleado() != null ? s.getNombreEmpleado() : "");
                    c1.setCellStyle(bodyStyle);

                    Cell c2 = row.createCell(2);
                    c2.setCellValue(s.getNombreCargo() != null ? s.getNombreCargo() : "");
                    c2.setCellStyle(bodyStyle);

                    Cell c3 = row.createCell(3);
                    c3.setCellValue(s.getNombreTipo() != null ? s.getNombreTipo() : "");
                    c3.setCellStyle(bodyStyle);

                    Cell c4 = row.createCell(4);
                    c4.setCellValue(s.getFechaInicio() != null ? s.getFechaInicio().toString() : "");
                    c4.setCellStyle(centerStyle);

                    Cell c5 = row.createCell(5);
                    c5.setCellValue(s.getFechaFin() != null ? s.getFechaFin().toString() : "");
                    c5.setCellStyle(centerStyle);

                    Cell c6 = row.createCell(6);
                    c6.setCellValue(s.getNombreEstado() != null ? s.getNombreEstado() : "");
                    c6.setCellStyle(centerStyle);
                }

                workbook.write(baos);
            } finally {
                workbook.close();
            }
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de solicitudes de permiso", e);
        }
    }

    private static void addCell(PdfPTable table, String text, com.lowagie.text.Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        table.addCell(cell);
    }
}
