package com.backendSupermercado.supermercasdo.shared.util;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
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

import com.backendSupermercado.supermercasdo.modules.productos.dto.PresentacionProductoResponseDto;
import com.backendSupermercado.supermercasdo.modules.productos.dto.ProductoResponseDto;
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
import com.lowagie.text.pdf.PdfWriter;

public class ReporteProductoUtil {

    private static final String TITULO_SISTEMA = "SUPERMERCADO LA MARTITA";
    private static final String SUBTITULO_SISTEMA = "Sistema de Gestión Comercial";
    private static final String TITULO_REPORTE = "REPORTE GENERAL DE PRODUCTOS";

    public static byte[] generarPdf(List<ProductoResponseDto> productos, String username) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());

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

            PdfPTable table = new PdfPTable(9);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 4f, 22f, 12f, 16f, 16f, 12f, 12f, 12f, 8f });

            com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            headerFont.setColor(255, 255, 255);

            String[] headers = { "#", "Nombre", "Código", "Categoría", "Subcategoría", "Marca", "Unidad base", "Precio", "Estado" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new java.awt.Color(46, 125, 50));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            com.lowagie.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            for (int i = 0; i < productos.size(); i++) {
                ProductoResponseDto p = productos.get(i);

                addCell(table, String.valueOf(i + 1), bodyFont, Element.ALIGN_CENTER);
                addCell(table, p.getNombre() != null ? p.getNombre() : "\u2014", bodyFont, Element.ALIGN_LEFT);
                addCell(table, p.getCodigoInterno() != null ? p.getCodigoInterno() : "\u2014", bodyFont,
                        Element.ALIGN_LEFT);
                addCell(table, p.getNombreCategoria() != null ? p.getNombreCategoria() : "\u2014", bodyFont,
                        Element.ALIGN_LEFT);
                addCell(table, p.getNombreSubcategoria() != null ? p.getNombreSubcategoria() : "\u2014", bodyFont,
                        Element.ALIGN_LEFT);
                addCell(table, p.getNombreMarca() != null ? p.getNombreMarca() : "\u2014", bodyFont, Element.ALIGN_LEFT);
                addCell(table,
                        p.getNombreUnidadBase() != null ? p.getNombreUnidadBase() : "\u2014", bodyFont,
                        Element.ALIGN_LEFT);
                addCell(table, precioFormateado(p), bodyFont, Element.ALIGN_CENTER);
                addCell(table, Boolean.TRUE.equals(p.getEstado()) ? "Activo" : "Inactivo", bodyFont,
                        Element.ALIGN_CENTER);
            }

            document.add(table);

            document.add(new Paragraph(" "));
            Paragraph total = new Paragraph("Total de registros: " + productos.size(),
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
            throw new RuntimeException("Error al generar PDF de productos", e);
        }

        return baos.toByteArray();
    }

    private static void addCell(PdfPTable table, String text, com.lowagie.text.Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        table.addCell(cell);
    }

    public static byte[] generarExcel(List<ProductoResponseDto> productos) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Productos");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
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

            String[] headers = { "#", "Nombre", "Código", "Categoría", "Subcategoría", "Marca", "Unidad base", "Precio", "Estado" };
            int[] colWidths = { 5, 30, 16, 22, 22, 16, 16, 15, 10 };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, colWidths[i] * 256);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < productos.size(); i++) {
                ProductoResponseDto p = productos.get(i);
                Row row = sheet.createRow(i + 1);
                row.setHeightInPoints(18);

                setCell(row, 0, String.valueOf(i + 1), centerStyle);
                setCell(row, 1, p.getNombre() != null ? p.getNombre() : "\u2014", bodyStyle);
                setCell(row, 2, p.getCodigoInterno() != null ? p.getCodigoInterno() : "\u2014", bodyStyle);
                setCell(row, 3, p.getNombreCategoria() != null ? p.getNombreCategoria() : "\u2014", bodyStyle);
                setCell(row, 4, p.getNombreSubcategoria() != null ? p.getNombreSubcategoria() : "\u2014", bodyStyle);
                setCell(row, 5, p.getNombreMarca() != null ? p.getNombreMarca() : "\u2014", bodyStyle);
                setCell(row, 6, p.getNombreUnidadBase() != null ? p.getNombreUnidadBase() : "\u2014", bodyStyle);
                setCell(row, 7, precioFormateado(p), centerStyle);
                setCell(row, 8, Boolean.TRUE.equals(p.getEstado()) ? "Activo" : "Inactivo", centerStyle);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de productos", e);
        }
    }

    private static void setCell(Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static String precioFormateado(ProductoResponseDto p) {
        if (p.getPresentaciones() == null || p.getPresentaciones().isEmpty()) {
            return "\u2014";
        }
        PresentacionProductoResponseDto principal = p.getPresentaciones().stream()
                .filter(x -> Boolean.TRUE.equals(x.getEsPrincipal()))
                .findFirst()
                .orElse(p.getPresentaciones().get(0));
        BigDecimal precio = principal != null ? principal.getPrecioVenta() : null;
        if (precio == null) {
            return "\u2014";
        }
        return "Bs " + precio.stripTrailingZeros().toPlainString();
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