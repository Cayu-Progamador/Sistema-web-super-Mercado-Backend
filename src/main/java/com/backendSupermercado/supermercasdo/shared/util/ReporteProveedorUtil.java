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

import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorListadoDto;
import com.backendSupermercado.supermercasdo.modules.proveedor.dto.ProveedorResponseDto;
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

public class ReporteProveedorUtil {

    private static final String TITULO_SISTEMA = "SUPERMERCADO LA MARTITA";
    private static final String SUBTITULO_SISTEMA = "Sistema de Gestión Comercial";
    private static final String TITULO_REPORTE = "REPORTE GENERAL DE PROVEEDORES";

    /* ===================== LISTADO PDF ===================== */

    public static byte[] generarPdf(List<ProveedorListadoDto> proveedores, String username) {
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

            agregarEncabezado(document);

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

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 4f, 22f, 12f, 16f, 22f, 14f, 10f });

            com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            headerFont.setColor(255, 255, 255);

            String[] headers = { "#", "Proveedor", "Tipo", "Documento", "Contacto", "Ciudad", "Estado" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new java.awt.Color(0, 96, 81));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            com.lowagie.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

            for (int i = 0; i < proveedores.size(); i++) {
                ProveedorListadoDto p = proveedores.get(i);

                addCell(table, String.valueOf(i + 1), bodyFont, Element.ALIGN_CENTER);
                addCell(table, p.getNombre() != null ? p.getNombre() : "\u2014", bodyFont, Element.ALIGN_LEFT);
                addCell(table, tipoTexto(p.getTipo()), bodyFont, Element.ALIGN_LEFT);
                addCell(table, p.getDocumento() != null ? p.getDocumento() : "\u2014", bodyFont, Element.ALIGN_LEFT);
                addCell(table, contactoTexto(p), bodyFont, Element.ALIGN_LEFT);
                addCell(table, p.getCiudad() != null ? p.getCiudad() : "\u2014", bodyFont, Element.ALIGN_LEFT);
                addCell(table, Boolean.TRUE.equals(p.getEstado()) ? "Activo" : "Inactivo", bodyFont,
                        Element.ALIGN_CENTER);
            }

            document.add(table);

            document.add(new Paragraph(" "));
            Paragraph total = new Paragraph("Total de registros: " + proveedores.size(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
            total.setSpacingBefore(10);
            document.add(total);

            agregarPie(document);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de proveedores", e);
        }

        return baos.toByteArray();
    }

    /* ===================== FICHA PDF (detalle) ===================== */

    public static byte[] generarPdfDetalle(ProveedorResponseDto proveedor, String username) {
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

            agregarEncabezado(document);

            Paragraph tituloFicha = new Paragraph("FICHA DEL PROVEEDOR",
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

            com.lowagie.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            com.lowagie.text.Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            String estado = Boolean.TRUE.equals(proveedor.getEstado()) ? "ACTIVO" : "INACTIVO";

            Paragraph sec1 = new Paragraph("INFORMACIÓN GENERAL", sectionFont);
            sec1.setSpacingAfter(8);
            document.add(sec1);

            document.add(new Paragraph("ID Proveedor  : " + (proveedor.getId() != null ? proveedor.getId() : "\u2014"), valueFont));
            document.add(new Paragraph("Tipo          : " + tipoTexto(proveedor.getTipo()), valueFont));
            document.add(new Paragraph("Fecha registro: " + (proveedor.getFechaRegistro() != null ? proveedor.getFechaRegistro() : "\u2014"), valueFont));
            document.add(new Paragraph("Estado        : " + estado, valueFont));

            document.add(new Paragraph(" "));

            if ("EMPRESA".equals(proveedor.getTipo()) && proveedor.getEmpresa() != null) {
                ProveedorResponseDto.EmpresaResponseDto e = proveedor.getEmpresa();

                Paragraph sec2 = new Paragraph("DATOS DE LA EMPRESA", sectionFont);
                sec2.setSpacingAfter(8);
                document.add(sec2);

                document.add(new Paragraph("Razón Social     : " + (e.getRazonSocial() != null ? e.getRazonSocial() : "\u2014"), valueFont));
                document.add(new Paragraph("Nombre Comercial : " + (e.getNombreComercial() != null ? e.getNombreComercial() : "\u2014"), valueFont));
                document.add(new Paragraph("NIT              : " + (e.getNit() != null ? e.getNit() : "\u2014"), valueFont));
                document.add(new Paragraph("Representante    : " + (e.getRepresentanteLegal() != null ? e.getRepresentanteLegal() : "\u2014"), valueFont));

                if (e.getContactos() != null && !e.getContactos().isEmpty()) {
                    document.add(new Paragraph(" "));
                    Paragraph sec3 = new Paragraph("CONTACTO", sectionFont);
                    sec3.setSpacingAfter(8);
                    document.add(sec3);
                    for (ProveedorResponseDto.ContactoResponseDto c : e.getContactos()) {
                        String dato = (c.getTelefono() != null ? "Tel: " + c.getTelefono() : "")
                                + (c.getTelefono() != null && c.getCorreo() != null ? "  |  " : "")
                                + (c.getCorreo() != null ? "Correo: " + c.getCorreo() : "");
                        if (!dato.isBlank()) {
                            document.add(new Paragraph(dato, valueFont));
                        }
                    }
                }
                if (e.getDireccion() != null) {
                    document.add(new Paragraph(" "));
                    Paragraph sec4 = new Paragraph("DIRECCIÓN", sectionFont);
                    sec4.setSpacingAfter(8);
                    document.add(sec4);
                    document.add(new Paragraph(direccionTexto(e.getDireccion()), valueFont));
                }
            } else if (proveedor.getPersona() != null) {
                ProveedorResponseDto.PersonaResponseDto p = proveedor.getPersona();

                Paragraph sec2 = new Paragraph("DATOS DE LA PERSONA", sectionFont);
                sec2.setSpacingAfter(8);
                document.add(sec2);

                document.add(new Paragraph("Nombre Completo  : " + (p.getNombreCompleto() != null ? p.getNombreCompleto() : "\u2014"), valueFont));
                document.add(new Paragraph("CI               : " + (p.getCi() != null ? p.getCi() : "\u2014"), valueFont));
                document.add(new Paragraph("Fecha Nacimiento : " + (p.getFechaNacimiento() != null ? p.getFechaNacimiento() : "\u2014"), valueFont));
                document.add(new Paragraph("Sexo             : " + (p.getSexo() != null ? p.getSexo() : "\u2014"), valueFont));

                document.add(new Paragraph(" "));
                Paragraph sec3 = new Paragraph("CONTACTO", sectionFont);
                sec3.setSpacingAfter(8);
                document.add(sec3);
                document.add(new Paragraph("Teléfono : " + (p.getTelefono() != null ? p.getTelefono() : "\u2014"), valueFont));
                document.add(new Paragraph("Correo   : " + (p.getCorreo() != null ? p.getCorreo() : "\u2014"), valueFont));

                if (p.getDireccion() != null) {
                    document.add(new Paragraph(" "));
                    Paragraph sec4 = new Paragraph("DIRECCIÓN", sectionFont);
                    sec4.setSpacingAfter(8);
                    document.add(sec4);
                    document.add(new Paragraph(direccionTexto(p.getDireccion()), valueFont));
                }
            }

            document.add(new Paragraph(" "));
            Paragraph sep = new Paragraph(
                    "\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550");
            sep.setAlignment(Element.ALIGN_CENTER);
            sep.setSpacingAfter(10);
            document.add(sep);

            document.add(new Paragraph(" "));
            Paragraph firmaLabel = new Paragraph("Firma Responsable", FontFactory.getFont(FontFactory.HELVETICA, 10));
            firmaLabel.setSpacingAfter(20);
            document.add(firmaLabel);

            Paragraph firmaLinea = new Paragraph("________________________", FontFactory.getFont(FontFactory.HELVETICA, 10));
            document.add(firmaLinea);

            document.add(new Paragraph(" "));
            Paragraph docFooter = new Paragraph(
                    "Documento generado automáticamente por el sistema.",
                    FontFactory.getFont(FontFactory.HELVETICA, 8));
            docFooter.setAlignment(Element.ALIGN_CENTER);
            docFooter.setSpacingBefore(4);
            document.add(docFooter);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar ficha PDF de proveedor", e);
        }

        return baos.toByteArray();
    }

    /* ===================== LISTADO EXCEL ===================== */

    public static byte[] generarExcel(List<ProveedorListadoDto> proveedores) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Proveedores");

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

            String[] headers = { "#", "Proveedor", "Tipo", "Documento", "Contacto", "Ciudad", "Estado" };
            int[] colWidths = { 5, 30, 12, 18, 30, 18, 10 };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, colWidths[i] * 256);
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < proveedores.size(); i++) {
                ProveedorListadoDto p = proveedores.get(i);
                Row row = sheet.createRow(i + 1);
                row.setHeightInPoints(18);

                setCell(row, 0, String.valueOf(i + 1), centerStyle);
                setCell(row, 1, p.getNombre() != null ? p.getNombre() : "\u2014", bodyStyle);
                setCell(row, 2, tipoTexto(p.getTipo()), bodyStyle);
                setCell(row, 3, p.getDocumento() != null ? p.getDocumento() : "\u2014", bodyStyle);
                setCell(row, 4, contactoTexto(p), bodyStyle);
                setCell(row, 5, p.getCiudad() != null ? p.getCiudad() : "\u2014", bodyStyle);
                setCell(row, 6, Boolean.TRUE.equals(p.getEstado()) ? "Activo" : "Inactivo", centerStyle);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de proveedores", e);
        }
    }

    /* ===================== FICHA EXCEL (detalle) ===================== */

    public static byte[] generarExcelDetalle(ProveedorResponseDto proveedor) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ficha Proveedor");

            CellStyle tituloStyle = workbook.createCellStyle();
            tituloStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
            tituloStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            tituloStyle.setFont(getHeaderFont(workbook));
            tituloStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle labelStyle = workbook.createCellStyle();
            labelStyle.setFont(getLabelFont(workbook));
            labelStyle.setBorderBottom(BorderStyle.THIN);
            labelStyle.setBorderLeft(BorderStyle.THIN);
            labelStyle.setBorderRight(BorderStyle.THIN);
            labelStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            labelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle valueStyle = workbook.createCellStyle();
            valueStyle.setFont(getBodyFont(workbook));
            valueStyle.setBorderBottom(BorderStyle.THIN);
            valueStyle.setBorderLeft(BorderStyle.THIN);
            valueStyle.setBorderRight(BorderStyle.THIN);

            sheet.setColumnWidth(0, 26 * 256);
            sheet.setColumnWidth(1, 60 * 256);

            Row t0 = sheet.createRow(0);
            Cell c = t0.createCell(0);
            c.setCellValue("FICHA DEL PROVEEDOR");
            c.setCellStyle(tituloStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));

            int rowIdx = 2;
            String estado = Boolean.TRUE.equals(proveedor.getEstado()) ? "Activo" : "Inactivo";

            rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "ID Proveedor", String.valueOf(proveedor.getId()));
            rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Tipo", tipoTexto(proveedor.getTipo()));
            rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Fecha registro", proveedor.getFechaRegistro());
            rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Estado", estado);

            if ("EMPRESA".equals(proveedor.getTipo()) && proveedor.getEmpresa() != null) {
                ProveedorResponseDto.EmpresaResponseDto e = proveedor.getEmpresa();

                rowIdx = escribirSeparador(sheet, rowIdx, labelStyle, valueStyle, "DATOS DE LA EMPRESA");
                rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Razón Social", e.getRazonSocial());
                rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Nombre Comercial", e.getNombreComercial());
                rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "NIT", e.getNit());
                rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Representante Legal", e.getRepresentanteLegal());

                if (e.getContactos() != null && !e.getContactos().isEmpty()) {
                    rowIdx = escribirSeparador(sheet, rowIdx, labelStyle, valueStyle, "CONTACTO");
                    for (ProveedorResponseDto.ContactoResponseDto co : e.getContactos()) {
                        String dato = (co.getTelefono() != null ? "Tel: " + co.getTelefono() : "")
                                + (co.getTelefono() != null && co.getCorreo() != null ? "  |  " : "")
                                + (co.getCorreo() != null ? "Correo: " + co.getCorreo() : "");
                        if (!dato.isBlank()) {
                            rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Contacto", dato);
                        }
                    }
                }
                if (e.getDireccion() != null) {
                    rowIdx = escribirSeparador(sheet, rowIdx, labelStyle, valueStyle, "DIRECCIÓN");
                    rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Dirección", direccionTexto(e.getDireccion()));
                }
            } else if (proveedor.getPersona() != null) {
                ProveedorResponseDto.PersonaResponseDto p = proveedor.getPersona();

                rowIdx = escribirSeparador(sheet, rowIdx, labelStyle, valueStyle, "DATOS DE LA PERSONA");
                rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Nombre Completo", p.getNombreCompleto());
                rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "CI", p.getCi());
                rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Fecha Nacimiento", p.getFechaNacimiento());
                rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Sexo", p.getSexo());

                rowIdx = escribirSeparador(sheet, rowIdx, labelStyle, valueStyle, "CONTACTO");
                rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Teléfono", p.getTelefono());
                rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Correo", p.getCorreo());

                if (p.getDireccion() != null) {
                    rowIdx = escribirSeparador(sheet, rowIdx, labelStyle, valueStyle, "DIRECCIÓN");
                    rowIdx = escribirFila(sheet, rowIdx, labelStyle, valueStyle, "Dirección", direccionTexto(p.getDireccion()));
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel de detalle de proveedor", e);
        }
    }

    private static int escribirFila(Sheet sheet, int rowIdx, CellStyle labelStyle, CellStyle valueStyle,
            String label, String value) {
        Row row = sheet.createRow(rowIdx);
        Cell lc = row.createCell(0);
        lc.setCellValue(label);
        lc.setCellStyle(labelStyle);
        Cell vc = row.createCell(1);
        vc.setCellValue(value != null ? value : "\u2014");
        vc.setCellStyle(valueStyle);
        return rowIdx + 1;
    }

    private static int escribirSeparador(Sheet sheet, int rowIdx, CellStyle labelStyle, CellStyle valueStyle,
            String titulo) {
        Row row = sheet.createRow(rowIdx);
        Cell lc = row.createCell(0);
        lc.setCellValue(titulo);
        lc.setCellStyle(labelStyle);
        Cell vc = row.createCell(1);
        vc.setCellValue("");
        vc.setCellStyle(valueStyle);
        return rowIdx + 1;
    }

    /* ===================== HELPERS ===================== */

    private static void agregarEncabezado(Document document) {
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
    }

    private static void agregarPie(Document document) {
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
    }

    private static String tipoTexto(String tipo) {
        return "EMPRESA".equals(tipo) ? "Empresa" : "Persona";
    }

    private static String contactoTexto(ProveedorListadoDto p) {
        StringBuilder sb = new StringBuilder();
        if (p.getContacto() != null && !p.getContacto().isBlank()) {
            sb.append(p.getContacto());
        }
        if (p.getTelefono() != null && !p.getTelefono().isBlank()) {
            if (sb.length() > 0) {
                sb.append(" · ");
            }
            sb.append(p.getTelefono());
        }
        if (p.getCorreo() != null && !p.getCorreo().isBlank()) {
            if (sb.length() > 0) {
                sb.append(" · ");
            }
            sb.append(p.getCorreo());
        }
        return sb.length() > 0 ? sb.toString() : "\u2014";
    }

    private static String direccionTexto(ProveedorResponseDto.DireccionResponseDto d) {
        StringBuilder sb = new StringBuilder();
        if (d.getCiudad() != null && !d.getCiudad().isBlank()) {
            sb.append(d.getCiudad());
        }
        if (d.getZona() != null && !d.getZona().isBlank()) {
            sb.append(sb.length() > 0 ? " · " : "").append(d.getZona());
        }
        if (d.getCalle() != null && !d.getCalle().isBlank()) {
            sb.append(sb.length() > 0 ? " · " : "").append(d.getCalle());
        }
        if (d.getNumero() != null && !d.getNumero().isBlank()) {
            sb.append(sb.length() > 0 ? " " : "").append(d.getNumero());
        }
        if (d.getReferencia() != null && !d.getReferencia().isBlank()) {
            sb.append(sb.length() > 0 ? " · " : "").append(d.getReferencia());
        }
        return sb.length() > 0 ? sb.toString() : "\u2014";
    }

    private static void addCell(PdfPTable table, String text, com.lowagie.text.Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private static void setCell(Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static Font getHeaderFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Calibri");
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        return font;
    }

    private static Font getLabelFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Calibri");
        font.setBold(true);
        return font;
    }

    private static Font getBodyFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Calibri");
        return font;
    }
}