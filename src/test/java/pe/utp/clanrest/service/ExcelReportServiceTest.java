package pe.utp.clanrest.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class ExcelReportServiceTest {
  private final ExcelReportService service = new ExcelReportService();

  @Test
  void generaLibroConEncabezadosYDatosDelEvento() throws Exception {
    var row = new ExcelReportService.EventReportRow(
        7L, LocalDateTime.of(2026, 11, 20, 18, 30), "Empresa Norte", "CONFIRMADO",
        new BigDecimal("4500.00"), new BigDecimal("1500.00"), new BigDecimal("3000.00"));

    byte[] bytes = service.generate(List.of(row));

    try (var workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
      var sheet = workbook.getSheet("Eventos");
      assertThat(sheet).isNotNull();
      assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Evento");
      assertThat(sheet.getRow(1).getCell(2).getStringCellValue()).isEqualTo("Empresa Norte");
      assertThat(sheet.getRow(1).getCell(4).getNumericCellValue()).isEqualTo(4500.00);
    }
  }

  @Test
  void protegeTextoQuePodriaInterpretarseComoFormula() throws Exception {
    var row = new ExcelReportService.EventReportRow(
        8L, LocalDateTime.of(2026, 12, 1, 12, 0), "=2+2", "CONFIRMADO",
        BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE);

    byte[] bytes = service.generate(List.of(row));

    try (var workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
      var clientCell = workbook.getSheet("Eventos").getRow(1).getCell(2);
      assertThat(clientCell.getCellType()).isEqualTo(CellType.STRING);
      assertThat(clientCell.getStringCellValue()).isEqualTo("'=2+2");
    }
  }

  @Test
  void limitaLaCantidadDeFilasParaProtegerLaMemoria() {
    var row = new ExcelReportService.EventReportRow(
        1L, LocalDateTime.now(), "Cliente", "CONFIRMADO",
        BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE);

    assertThatThrownBy(() -> service.generate(Collections.nCopies(ExcelReportService.MAX_ROWS + 1, row)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("máximo");
  }
}
