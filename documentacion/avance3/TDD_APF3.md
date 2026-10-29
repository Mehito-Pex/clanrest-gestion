# Evidencia TDD del Avance 3

## Funcionalidad seleccionada

Exportación de eventos y saldos a Excel desde la pantalla de reportes.

## RED

Se escribieron tres pruebas antes del servicio. La compilación falló porque `ExcelReportService` no existía. La salida completa está en `evidencias/tdd-red.txt`.

Casos definidos:

1. El libro contiene encabezados y datos monetarios.
2. Los textos similares a fórmulas permanecen como texto seguro.
3. La exportación rechaza más de 10 000 filas.

## GREEN

Se implementó `ExcelReportService` con el código necesario para satisfacer los casos. El resultado fue `Tests run: 3, Failures: 0, Errors: 0`. La salida completa está en `evidencias/tdd-green.txt`.

## Integración

Se añadió una prueba con MockMvc que solicita `/reports/export/xlsx`, verifica el tipo de contenido, abre la respuesta con Apache POI y confirma la hoja `Eventos`. El conjunto final contiene 20 pruebas aprobadas.

## Refactorización

La exportación utiliza un record `EventReportRow` para desacoplar el archivo del modelo JPA. Los métodos privados concentran estilos, montos y normalización. El controlador solo prepara filas autorizadas y entrega la respuesta HTTP.
