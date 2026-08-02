package com.backendSupermercado.supermercasdo.modules.asistencia.service.impl;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaFiltrosDto;
import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AsistenciaExportService {

    private static final DeviceRgb LOGO_GREEN = new DeviceRgb(42, 92, 26);
    private static final DeviceRgb HEADER_BG = new DeviceRgb(42, 92, 26);
    private static final DeviceRgb HEADER_FG = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb EVEN_ROW = new DeviceRgb(240, 247, 235);

    private final AsistenciaAdminServiceImpl adminService;

    public byte[] exportarPDF(AsistenciaFiltrosDto filtros, String busqueda, Long idTurno, String sortBy, String sortDir) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            document.add(new Paragraph("Reporte de Asistencia")
                    .setFont(boldFont).setFontSize(18).setFontColor(LOGO_GREEN)
                    .setTextAlignment(TextAlignment.CENTER));

            String fechaStr = filtros.getFechaDesde() != null
                    ? filtros.getFechaDesde() + (filtros.getFechaHasta() != null && !filtros.getFechaHasta().equals(filtros.getFechaDesde()) ? " al " + filtros.getFechaHasta() : "")
                    : LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            document.add(new Paragraph("Fecha: " + fechaStr)
                    .setFont(font).setFontSize(10).setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            List<AsistenciaResponseDto> data = obtenerDatos(filtros, busqueda, idTurno, sortBy, sortDir);

            Table table = new Table(UnitValue.createPercentArray(new float[]{12, 18, 10, 10, 10, 10, 10, 10, 10}));
            table.setWidth(UnitValue.createPercentValue(100));

            String[] headers = {"Código", "Empleado", "Turno", "Entrada", "Salida", "Estado", "Retraso", "Hrs.Trab.", "Cargo"};
            for (String h : headers) {
                Paragraph p = new Paragraph(h).setFont(boldFont).setFontSize(8).setFontColor(HEADER_FG);
                Cell cell = new Cell();
                cell.add(p);
                cell.setBackgroundColor(HEADER_BG);
                cell.setTextAlignment(TextAlignment.CENTER);
                cell.setBorder(new SolidBorder(HEADER_FG, 0.5f));
                table.addHeaderCell(cell);
            }

            int rowIdx = 0;
            for (AsistenciaResponseDto dto : data) {
                if (rowIdx % 2 == 1) {
                    addCell(table, dto.getIdAsistencia() != null ? String.valueOf(dto.getIdAsistencia()) : "", font, EVEN_ROW);
                    addCell(table, dto.getNombreEmpleado() != null ? dto.getNombreEmpleado() : "", font, EVEN_ROW);
                    addCell(table, dto.getTurnoNombre() != null ? dto.getTurnoNombre() : "--", font, EVEN_ROW);
                    addCell(table, dto.getHoraEntrada() != null ? dto.getHoraEntrada().toString() : "--", font, EVEN_ROW);
                    addCell(table, dto.getHoraSalida() != null ? dto.getHoraSalida().toString() : "--", font, EVEN_ROW);
                    addCell(table, dto.getEstado() != null ? dto.getEstado() : "", font, EVEN_ROW);
                    addCell(table, dto.getMinutosRetraso() != null ? dto.getMinutosRetraso() + " min" : "0", font, EVEN_ROW);
                    addCell(table, dto.getHorasTrabajadas() != null ? dto.getHorasTrabajadas().toString() : "0", font, EVEN_ROW);
                    addCell(table, dto.getCargo() != null ? dto.getCargo() : "--", font, EVEN_ROW);
                } else {
                    addCell(table, dto.getIdAsistencia() != null ? String.valueOf(dto.getIdAsistencia()) : "", font, null);
                    addCell(table, dto.getNombreEmpleado() != null ? dto.getNombreEmpleado() : "", font, null);
                    addCell(table, dto.getTurnoNombre() != null ? dto.getTurnoNombre() : "--", font, null);
                    addCell(table, dto.getHoraEntrada() != null ? dto.getHoraEntrada().toString() : "--", font, null);
                    addCell(table, dto.getHoraSalida() != null ? dto.getHoraSalida().toString() : "--", font, null);
                    addCell(table, dto.getEstado() != null ? dto.getEstado() : "", font, null);
                    addCell(table, dto.getMinutosRetraso() != null ? dto.getMinutosRetraso() + " min" : "0", font, null);
                    addCell(table, dto.getHorasTrabajadas() != null ? dto.getHorasTrabajadas().toString() : "0", font, null);
                    addCell(table, dto.getCargo() != null ? dto.getCargo() : "--", font, null);
                }
                rowIdx++;
            }

            document.add(table);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }

        return baos.toByteArray();
    }

    public byte[] exportarExcel(AsistenciaFiltrosDto filtros, String busqueda, Long idTurno, String sortBy, String sortDir) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Asistencia");

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 10);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            CellStyle altStyle = workbook.createCellStyle();
            altStyle.cloneStyleFrom(cellStyle);
            altStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"Código", "Empleado", "Turno", "Entrada", "Salida", "Estado", "Retraso", "Horas Trab.", "Cargo"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<AsistenciaResponseDto> data = obtenerDatos(filtros, busqueda, idTurno, sortBy, sortDir);

            int rowNum = 1;
            for (AsistenciaResponseDto dto : data) {
                Row row = sheet.createRow(rowNum);
                CellStyle style = (rowNum % 2 == 0) ? altStyle : cellStyle;

                setCell(row, 0, dto.getIdAsistencia() != null ? String.valueOf(dto.getIdAsistencia()) : "", style);
                setCell(row, 1, dto.getNombreEmpleado() != null ? dto.getNombreEmpleado() : "", style);
                setCell(row, 2, dto.getTurnoNombre() != null ? dto.getTurnoNombre() : "--", style);
                setCell(row, 3, dto.getHoraEntrada() != null ? dto.getHoraEntrada().toString() : "--", style);
                setCell(row, 4, dto.getHoraSalida() != null ? dto.getHoraSalida().toString() : "--", style);
                setCell(row, 5, dto.getEstado() != null ? dto.getEstado() : "", style);
                setCell(row, 6, dto.getMinutosRetraso() != null ? dto.getMinutosRetraso() + " min" : "0", style);
                setCell(row, 7, dto.getHorasTrabajadas() != null ? dto.getHorasTrabajadas().toString() : "0", style);
                setCell(row, 8, dto.getCargo() != null ? dto.getCargo() : "--", style);
                rowNum++;
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }

    private List<AsistenciaResponseDto> obtenerDatos(AsistenciaFiltrosDto filtros, String busqueda, Long idTurno, String sortBy, String sortDir) {
        Sort sort = sortDir != null && sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy != null ? sortBy : "fecha").ascending()
                : Sort.by(sortBy != null ? sortBy : "fecha").descending();
        PageRequest pageable = PageRequest.of(0, 10000, sort);
        Page<AsistenciaResponseDto> page = adminService.listarAsistencias(filtros, busqueda, idTurno, pageable);
        return page.getContent();
    }

    private void addCell(Table table, String value, PdfFont font, DeviceRgb bgColor) {
        Paragraph p = new Paragraph(value != null ? value : "").setFont(font).setFontSize(7);
        p.setTextAlignment(TextAlignment.CENTER);
        Cell cell = new Cell();
        cell.add(p);
        if (bgColor != null) {
            cell.setBackgroundColor(bgColor);
        }
        cell.setBorder(Border.NO_BORDER);
        table.addCell(cell);
    }

    private void setCell(Row row, int col, String value, CellStyle style) {
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
}