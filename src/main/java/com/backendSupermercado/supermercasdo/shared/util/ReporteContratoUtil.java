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

import com.backendSupermercado.supermercasdo.modules.contrato.dto.ContratoListadoDto;
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

public class ReporteContratoUtil {

    private static final String TITULO_SISTEMA = "SUPERMERCADO LA MARTITA";
    private static final String SUBTITULO_SISTEMA = "Sistema de Gesti\u00f3n Comercial";
    private static final String TITULO_REPORTE = "REPORTE GENERAL DE CONTRATOS";

    public static byte[] generarPdf(List<ContratoListadoDto> contratos, String username) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            writer.setPageEvent(new PdfPageEventHelper() {
                private com.lowagie.text.Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    String texto = "P\u00e1gina " + writer.getCurrentPageNumber();
                    com.lowagie.text.pdf.ColumnText.showTextAligned(
                            writer.getDirectContent(),
                            Element.ALIGN_RIGHT,
                            new Phrase(texto, footerFont),
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

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 3f, 18f, 14f, 12f, 12f, 10f, 12f, 10f });

            com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            headerFont.setColor(255, 255, 255);

            String[] headers = { "#", "Empleado", "Cargo", "Tipo Contrato", "Tipo Jornada", "Sueldo Base",
                    "Fecha Inicio", "Estado" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new java.awt.Color(0, 96, 81));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            com.lowagie.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            for (int i = 0; i < contratos.size(); i++) {
                ContratoListadoDto c = contratos.get(i);
                String empleado = "";
                if (c.getEmpleado() != null) {
                    empleado = (c.getEmpleado().getNombres() != null ? c.getEmpleado().getNombres() : "")
                            + " " + (c.getEmpleado().getApellidos() != null ? c.getEmpleado().getApellidos() : "");
                    empleado = empleado.trim();
                }

                addCell(table, String.valueOf(i + 1), bodyFont, Element.ALIGN_CENTER);
                addCell(table, !empleado.isEmpty() ? empleado : "\u2014", bodyFont, Element.ALIGN_LEFT);
                addCell(table, c.getCargoNombre() != null ? c.getCargoNombre() : "\u2014", bodyFont,
                        Element.ALIGN_LEFT);
                addCell(table, c.getTipoContratoNombre() != null ? c.getTipoContratoNombre() : "\u2014",
                        bodyFont, Element.ALIGN_LEFT);
                addCell(table, c.getTipoJornadaNombre() != null ? c.getTipoJornadaNombre() : "\u2014",
                        bodyFont, Element.ALIGN_LEFT);
                addCell(table, c.getSueldoBase() != null ? "$ " + c.getSueldoBase().toString() : "\u2014",
                        bodyFont, Element.ALIGN_RIGHT);
                addCell(table, c.getFechaInicio() != null ? c.getFechaInicio().toString() : "\u2014",
                        bodyFont, Element.ALIGN_CENTER);
                addCell(table, c.getEstado() != null ? c.getEstado() : "\u2014", bodyFont,
                        Element.ALIGN_CENTER);
            }

            document.add(table);

            document.add(new Paragraph(" "));
            Paragraph total = new Paragraph("Total de registros: " + contratos.size(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
            total.setSpacingBefore(10);
            document.add(total);

            Paragraph footerLine = new Paragraph(
                    "__________________________________________________");
            footerLine.setAlignment(Element.ALIGN_CENTER);
            footerLine.setSpacingBefore(8);
            document.add(footerLine);

            Paragraph docFooter = new Paragraph(
                    "Documento generado autom\u00e1ticamente por el Sistema de Gesti\u00f3n.",
                    FontFactory.getFont(FontFactory.HELVETICA, 8));
            docFooter.setAlignment(Element.ALIGN_CENTER);
            docFooter.setSpacingBefore(4);
            document.add(docFooter);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de contratos", e);
        }

        return baos.toByteArray();
    }

    public static byte[] generarExcel(List<ContratoListadoDto> contratos) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contratos");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
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

            String[] headers = { "#", "Empleado", "Cargo", "Tipo Contrato", "Tipo Jornada", "Sueldo Base",
                    "Fecha Inicio", "Estado" };
            int[] colWidths = { 5, 35, 25, 20, 20, 15, 15, 12 };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, colWidths[i] * 256);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < contratos.size(); i++) {
                ContratoListadoDto c = contratos.get(i);
                Row row = sheet.createRow(i + 1);
                row.setHeightInPoints(18);

                String empleado = "";
                if (c.getEmpleado() != null) {
                    empleado = (c.getEmpleado().getNombres() != null ? c.getEmpleado().getNombres() : "")
                            + " " + (c.getEmpleado().getApellidos() != null ? c.getEmpleado().getApellidos() : "");
                    empleado = empleado.trim();
                }

                Cell c0 = row.createCell(0);
                c0.setCellValue(i + 1);
                c0.setCellStyle(centerStyle);

                Cell c1 = row.createCell(1);
                c1.setCellValue(!empleado.isEmpty() ? empleado : "\u2014");
                c1.setCellStyle(bodyStyle);

                Cell c2 = row.createCell(2);
                c2.setCellValue(c.getCargoNombre() != null ? c.getCargoNombre() : "\u2014");
                c2.setCellStyle(bodyStyle);

                Cell c3 = row.createCell(3);
                c3.setCellValue(c.getTipoContratoNombre() != null ? c.getTipoContratoNombre() : "\u2014");
                c3.setCellStyle(bodyStyle);

                Cell c4 = row.createCell(4);
                c4.setCellValue(c.getTipoJornadaNombre() != null ? c.getTipoJornadaNombre() : "\u2014");
                c4.setCellStyle(bodyStyle);

                Cell c5 = row.createCell(5);
                c5.setCellValue(c.getSueldoBase() != null ? "$ " + c.getSueldoBase().toString() : "\u2014");
                c5.setCellStyle(centerStyle);

                Cell c6 = row.createCell(6);
                c6.setCellValue(c.getFechaInicio() != null ? c.getFechaInicio().toString() : "\u2014");
                c6.setCellStyle(centerStyle);

                Cell c7 = row.createCell(7);
                c7.setCellValue(c.getEstado() != null ? c.getEstado() : "\u2014");
                c7.setCellStyle(centerStyle);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de contratos", e);
        }
    }

    private static void addCell(PdfPTable table, String text, com.lowagie.text.Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        table.addCell(cell);
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
