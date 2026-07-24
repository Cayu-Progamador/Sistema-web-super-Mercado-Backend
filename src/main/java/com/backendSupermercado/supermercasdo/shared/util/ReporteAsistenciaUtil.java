package com.backendSupermercado.supermercasdo.shared.util;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.backendSupermercado.supermercasdo.modules.asistencia.dto.AsistenciaResponseDto;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class ReporteAsistenciaUtil {

    private static final String TITULO_SISTEMA = "SUPERMERCADO LA MARTITA";
    private static final String TITULO_REPORTE = "REPORTE SEMANAL DE ASISTENCIA";

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
    private static final Font CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final Font RESALT_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    public static byte[] generarPdf(List<AsistenciaResponseDto> registros,
                                     String nombreEmpleado,
                                     LocalDate desde,
                                     LocalDate hasta,
                                     String username) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            writer.setPageEvent(new PdfPageEventHelper() {
                private Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

                @Override
                public void onEndPage(PdfWriter writer, Document document) {
                    String texto = "Página " + writer.getCurrentPageNumber() + " | Generado por: " + username
                            + " | " + LocalDate.now().format(DATE_FMT);
                    com.lowagie.text.pdf.ColumnText.showTextAligned(
                            writer.getDirectContent(),
                            Element.ALIGN_RIGHT,
                            new Phrase(texto, footerFont),
                            document.right(),
                            document.bottom() - 10,
                            0);
                }
            });

            agregarEncabezado(document, nombreEmpleado, desde, hasta);
            agregarTabla(document, registros);
            agregarResumen(document, registros);

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF de asistencia", e);
        }

        return baos.toByteArray();
    }

    private static void agregarEncabezado(Document doc, String nombre, LocalDate desde, LocalDate hasta) {
        Paragraph titulo = new Paragraph(TITULO_SISTEMA, TITLE_FONT);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Paragraph reporte = new Paragraph(TITULO_REPORTE, SUBTITLE_FONT);
        reporte.setAlignment(Element.ALIGN_CENTER);
        reporte.setSpacingAfter(4);
        doc.add(reporte);

        Paragraph empleado = new Paragraph("Empleado: " + nombre, RESALT_FONT);
        empleado.setAlignment(Element.ALIGN_LEFT);
        empleado.setSpacingBefore(10);
        doc.add(empleado);

        Paragraph periodo = new Paragraph(
                "Período: " + desde.format(DATE_FMT) + " - " + hasta.format(DATE_FMT),
                SUBTITLE_FONT);
        periodo.setAlignment(Element.ALIGN_LEFT);
        periodo.setSpacingAfter(10);
        doc.add(periodo);
    }

    private static void agregarTabla(Document doc, List<AsistenciaResponseDto> registros) {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10);
        table.setWidths(new float[]{1.5f, 1.5f, 1.5f, 1.5f, 1.5f, 2f});

        String[] headers = {"Día", "Fecha", "Entrada", "Salida", "Horas", "Estado"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, HEADER_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            cell.setGrayFill(0.9f);
            table.addCell(cell);
        }

        if (registros.isEmpty()) {
            PdfPCell cell = new PdfPCell(new Phrase("No hay registros para este período", CELL_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            cell.setColspan(6);
            table.addCell(cell);
        } else {
            for (AsistenciaResponseDto r : registros) {
                String[] dias = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
                String dia = r.getFecha() != null ? dias[r.getFecha().getDayOfWeek().getValue() % 7] : "--";
                String fecha = r.getFecha() != null ? r.getFecha().format(DATE_FMT) : "--";
                String entrada = r.getHoraEntrada() != null ? r.getHoraEntrada().format(TIME_FMT) : "--";
                String salida = r.getHoraSalida() != null ? r.getHoraSalida().format(TIME_FMT) : "--";
                String horas = "--";
                if (r.getHoraEntrada() != null && r.getHoraSalida() != null) {
                    long mins = java.time.Duration.between(r.getHoraEntrada(), r.getHoraSalida()).toMinutes();
                    if (mins > 0) {
                        horas = (mins / 60) + "h " + (mins % 60) + "m";
                    }
                }
                String estado = r.getEstado() != null ? r.getEstado() : "FALTA";
                String estadoLabel = switch (estado) {
                    case "PRESENTE" -> "En turno";
                    case "COMPLETO" -> "A tiempo";
                    case "TARDANZA" -> "Tardanza";
                    case "FALTA" -> "Falta";
                    case "JUSTIFICADO" -> "Justificado";
                    default -> estado;
                };

                table.addCell(crearCelda(dia));
                table.addCell(crearCelda(fecha));
                table.addCell(crearCelda(entrada));
                table.addCell(crearCelda(salida));
                table.addCell(crearCelda(horas));
                table.addCell(crearCelda(estadoLabel));
            }
        }

        doc.add(table);
    }

    private static PdfPCell crearCelda(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, CELL_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4);
        return cell;
    }

    private static void agregarResumen(Document doc, List<AsistenciaResponseDto> registros) {
        long asistencias = registros.stream()
                .filter(r -> r.getHoraEntrada() != null)
                .count();
        long tardanzas = registros.stream()
                .filter(r -> "TARDANZA".equals(r.getEstado()))
                .count();
        long faltas = registros.stream()
                .filter(r -> "FALTA".equals(r.getEstado()) || (r.getHoraEntrada() == null && r.getEstado() == null))
                .count();
        long justificados = registros.stream()
                .filter(r -> "JUSTIFICADO".equals(r.getEstado()))
                .count();

        long totalMinutos = registros.stream()
                .filter(r -> r.getHoraEntrada() != null && r.getHoraSalida() != null)
                .mapToLong(r -> java.time.Duration.between(r.getHoraEntrada(), r.getHoraSalida()).toMinutes())
                .filter(m -> m > 0)
                .sum();
        String horasStr = (totalMinutos / 60) + "h " + (totalMinutos % 60) + "m";

        Paragraph resumen = new Paragraph("RESUMEN", RESALT_FONT);
        resumen.setSpacingBefore(6);
        doc.add(resumen);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(60);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidths(new float[]{1, 1, 1, 1});

        table.addCell(crearCeldaResumen("Asistencias", String.valueOf(asistencias)));
        table.addCell(crearCeldaResumen("Tardanzas", String.valueOf(tardanzas)));
        table.addCell(crearCeldaResumen("Faltas", String.valueOf(faltas)));
        table.addCell(crearCeldaResumen("Justificados", String.valueOf(justificados)));
        doc.add(table);

        Paragraph horasTotal = new Paragraph("Total horas trabajadas: " + horasStr, SUBTITLE_FONT);
        horasTotal.setSpacingBefore(4);
        doc.add(horasTotal);
    }

    private static PdfPCell crearCeldaResumen(String label, String valor) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA, 9);
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(label, f);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        Paragraph v = new Paragraph(valor, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        v.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(v);
        cell.setPadding(6);
        return cell;
    }
}
