package pe.utp.clanrest.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Genera el reporte operativo de eventos en formato XLSX. */
@Service
public class ExcelReportService {
  public static final int MAX_ROWS = 10_000;
  private static final Logger log = LoggerFactory.getLogger(ExcelReportService.class);
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
  private static final List<String> HEADERS =
      ImmutableList.of("Evento", "Fecha", "Cliente", "Estado", "Total", "Cobrado", "Saldo");

  /** Fila independiente del modelo de persistencia para mantener aislada la exportación. */
  public record EventReportRow(
      Long eventId,
      LocalDateTime date,
      String client,
      String status,
      BigDecimal total,
      BigDecimal paid,
      BigDecimal balance) {}

  /**
   * Construye un libro de Excel en memoria.
   *
   * @param rows filas ya filtradas y autorizadas por el controlador
   * @return contenido binario del archivo XLSX
   * @throws IOException cuando Apache POI no puede escribir el libro
   */
  public byte[] generate(List<EventReportRow> rows) throws IOException {
    Preconditions.checkNotNull(rows, "Las filas son obligatorias");
    Preconditions.checkArgument(rows.size() <= MAX_ROWS, "Se permite un máximo de %s filas", MAX_ROWS);

    try (var workbook = new XSSFWorkbook(); var output = new ByteArrayOutputStream()) {
      var sheet = workbook.createSheet("Eventos");
      sheet.createFreezePane(0, 1);
      var headerStyle = headerStyle(workbook);
      var moneyStyle = moneyStyle(workbook);

      var header = sheet.createRow(0);
      for (int column = 0; column < HEADERS.size(); column++) {
        var cell = header.createCell(column);
        cell.setCellValue(HEADERS.get(column));
        cell.setCellStyle(headerStyle);
      }

      int index = 1;
      for (var source : rows) {
        var row = sheet.createRow(index++);
        row.createCell(0).setCellValue(source.eventId());
        row.createCell(1).setCellValue(source.date().format(DATE_FORMAT));
        row.createCell(2).setCellValue(safeText(source.client()));
        row.createCell(3).setCellValue(safeText(source.status()));
        writeMoney(row.createCell(4), source.total(), moneyStyle);
        writeMoney(row.createCell(5), source.paid(), moneyStyle);
        writeMoney(row.createCell(6), source.balance(), moneyStyle);
      }

      sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, Math.max(1, rows.size()), 0, 6));
      int[] widths = {12, 20, 34, 18, 16, 16, 16};
      for (int column = 0; column < widths.length; column++) sheet.setColumnWidth(column, widths[column] * 256);

      workbook.write(output);
      log.info("Reporte XLSX generado con {} eventos", rows.size());
      return output.toByteArray();
    }
  }

  private CellStyle headerStyle(XSSFWorkbook workbook) {
    var style = workbook.createCellStyle();
    style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    var font = workbook.createFont();
    font.setBold(true);
    font.setColor(IndexedColors.WHITE.getIndex());
    style.setFont(font);
    return style;
  }

  private CellStyle moneyStyle(XSSFWorkbook workbook) {
    var style = workbook.createCellStyle();
    style.setDataFormat(workbook.createDataFormat().getFormat("S/ #,##0.00"));
    return style;
  }

  private void writeMoney(org.apache.poi.ss.usermodel.Cell cell, BigDecimal value, CellStyle style) {
    cell.setCellValue(value == null ? 0 : value.doubleValue());
    cell.setCellStyle(style);
  }

  private String safeText(String value) {
    String normalized = StringUtils.abbreviate(StringUtils.trimToEmpty(value), 200);
    return normalized.matches("^[=+@-].*") ? "'" + normalized : normalized;
  }
}
