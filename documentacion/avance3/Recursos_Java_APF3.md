# Recursos Java incorporados

| Recurso | Uso implementado | Beneficio | Evidencia |
| --- | --- | --- | --- |
| Google Guava | `ImmutableList` para encabezados y `Preconditions` para el límite de filas | Estructura inmutable y validación explícita | `ExcelReportService.java` |
| Apache POI | Creación de archivo XLSX con estilos, filtros, columnas y formato monetario | Reporte compatible con Excel | Endpoint `/reports/export/xlsx` |
| Apache Commons Lang | Normalización, recorte y límite del texto exportado | Reduce datos inválidos y celdas excesivas | Método `safeText` |
| Logback | Registro del número de eventos exportados sin incluir datos personales | Trazabilidad operativa sin registrar secretos | `logback-spring.xml` y logger del servicio |

Las versiones quedan declaradas en `pom.xml`. Spring Boot administra las versiones de Commons Lang y Logback; Guava y Apache POI tienen versiones explícitas para asegurar una construcción reproducible.
