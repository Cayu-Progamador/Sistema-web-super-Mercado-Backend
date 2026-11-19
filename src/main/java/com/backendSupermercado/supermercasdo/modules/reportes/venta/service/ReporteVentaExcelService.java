package com.backendSupermercado.supermercasdo.modules.reportes.venta.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.backendSupermercado.supermercasdo.modules.reportes.venta.dto.ReporteVentaExportacion;

@Service
public class ReporteVentaExcelService {

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generar(ReporteVentaExportacion reporte) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Sheet hoja = workbook.createSheet("Reporte de ventas");
            CellStyle titulo = estiloTitulo(workbook);
            CellStyle encabezado = estiloEncabezado(workbook);
            CellStyle cuerpo = estiloCuerpo(workbook);

            Row filaTitulo = hoja.createRow(0);
            filaTitulo.createCell(0).setCellValue("SUPERMERCADO LA MARTITA - " + reporte.titulo().toUpperCase());
            filaTitulo.getCell(0).setCellStyle(titulo);
            Row filaGenerado = hoja.createRow(1);
            filaGenerado.createCell(0).setCellValue("Generado: " + LocalDateTime.now().format(FECHA_HORA));
            Row filaFiltros = hoja.createRow(2);
            filaFiltros.createCell(0).setCellValue(reporte.filtros());

            int inicioTabla = 4;
            Row filaCabecera = hoja.createRow(inicioTabla);
            for (int columna = 0; columna < reporte.columnas().size(); columna++) {
                org.apache.poi.ss.usermodel.Cell celda = filaCabecera.createCell(columna);
                celda.setCellValue(reporte.columnas().get(columna));
                celda.setCellStyle(encabezado);
            }
            for (int indice = 0; indice < reporte.filas().size(); indice++) {
                Row fila = hoja.createRow(inicioTabla + indice + 1);
                List<String> valores = reporte.filas().get(indice);
                for (int columna = 0; columna < valores.size(); columna++) {
                    org.apache.poi.ss.usermodel.Cell celda = fila.createCell(columna);
                    celda.setCellValue(valores.get(columna));
                    celda.setCellStyle(cuerpo);
                }
            }

            int ultimaFila = Math.max(inicioTabla, inicioTabla + reporte.filas().size());
            hoja.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(
                    inicioTabla, ultimaFila, 0, reporte.columnas().size() - 1));
            hoja.createFreezePane(0, inicioTabla + 1);
            for (int columna = 0; columna < reporte.columnas().size(); columna++) {
                hoja.autoSizeColumn(columna);
                int ancho = Math.min(hoja.getColumnWidth(columna) + 700, 50 * 256);
                hoja.setColumnWidth(columna, ancho);
            }
            workbook.write(salida);
            return salida.toByteArray();
        } catch (Exception exception) {
            throw new IllegalStateException("No se pudo generar el reporte de ventas en Excel", exception);
        }
    }

    private CellStyle estiloTitulo(XSSFWorkbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        Font fuente = workbook.createFont();
        fuente.setBold(true);
        fuente.setFontHeightInPoints((short) 14);
        estilo.setFont(fuente);
        return estilo;
    }

    private CellStyle estiloEncabezado(XSSFWorkbook workbook) {
        CellStyle estilo = bordes(workbook);
        Font fuente = workbook.createFont();
        fuente.setBold(true);
        estilo.setFont(fuente);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        estilo.setWrapText(true);
        return estilo;
    }

    private CellStyle estiloCuerpo(XSSFWorkbook workbook) {
        CellStyle estilo = bordes(workbook);
        estilo.setVerticalAlignment(VerticalAlignment.CENTER);
        return estilo;
    }

    private CellStyle bordes(XSSFWorkbook workbook) {
        CellStyle estilo = workbook.createCellStyle();
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
        return estilo;
    }
}
