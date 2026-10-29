# Entrega del Avance de Proyecto Final 3

## Proyecto

ClanRest Gestión es una aplicación web para organizar clientes, catálogo, cotizaciones, eventos, inventario, pagos, alertas, reportes, usuarios y auditoría de una empresa de catering.

## Resultado verificable

- Java 21 y Spring Boot 3.5.6.
- Arquitectura MVC con servicios y repositorios DAO mediante Spring Data JPA.
- Seguridad por roles ADMIN, COORDINADOR y ALMACEN, BCrypt, CSRF y validación de entradas.
- Exportación de eventos a Excel con Apache POI.
- Uso real de Guava, Apache Commons y Logback en la exportación.
- 20 pruebas automatizadas aprobadas.
- Cobertura JaCoCo: 82.8 % de instrucciones y 50 % de ramas.
- Javadoc y reporte de cobertura incluidos como archivos ZIP.

## Archivos de esta entrega

- `Informe_Avance_3_ClanRest.docx`: informe principal.
- `Sustentacion_Avance_3_ClanRest.pptx`: presentación para 10 minutos.
- `Arquitectura_APF3.md`: arquitectura y aplicación de patrones.
- `Matriz_Cobertura_APF3.md`: trazabilidad de alcance a pantallas y pruebas.
- `Recursos_Java_APF3.md`: uso de las cuatro librerías solicitadas.
- `Seguridad_APF3.md`: controles incorporados.
- `TDD_APF3.md`: evidencia RED y GREEN.
- `Guion_Sustentacion_APF3.md`: distribución sugerida para los integrantes.
- `Javadoc_ClanRest.zip`: documentación HTML generada.
- `Cobertura_JaCoCo.zip`: reporte HTML de cobertura.
- `evidencias/`: resultados de pruebas y capturas del aplicativo.

## Ejecución

Desde la raíz del proyecto:

```powershell
mvn clean test package javadoc:javadoc
java -jar target/clanrest-1.0.0.jar
```

Abrir `http://127.0.0.1:8080`. Las cuentas demo están documentadas en el README principal.

## Demostración recomendada

1. Iniciar sesión como ADMIN.
2. Mostrar el panel principal y los módulos del menú.
3. Abrir Reportes y descargar `clanrest-eventos.xlsx`.
4. Explicar `ExcelReportService` y las cuatro librerías Java.
5. Mostrar las evidencias TDD RED y GREEN.
6. Ejecutar `mvn test` y mostrar las 20 pruebas aprobadas.
7. Abrir GitHub y enseñar el historial, los archivos del avance y la verificación automática.
