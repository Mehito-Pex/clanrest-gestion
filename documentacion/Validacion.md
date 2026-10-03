# Evidencia de verificación

Entorno: Windows, Temurin Java 21.0.12.1, Maven 3.9.11, Spring Boot 3.5.6 y H2. Suite ClanRestIntegrationTest: 16 pruebas, 0 fallos, 0 errores. Las pruebas cubren ciclo comercial, conservación de precios, unicidad de evento, propuesta vencida, cobro excesivo/fracción de centavo, inventario negativo, receta inválida, estados y cierre con saldo, último ADMIN, 21 rutas de administración, roles, login real, CSRF y CSV seguro.

Se ejecutó el JAR y se capturaron 16 vistas de escritorio más una vista móvil con Chrome. Las imágenes están en capturas/. La carga de pantallas se prueba con MockMvc, y las capturas confirman una ejecución real en navegador. No se afirma aceptación de usuarios, rendimiento bajo carga, disponibilidad de servidor, prueba de restauración ni ejecución contra MySQL.

## Pendientes de aceptación
Confirmar impuestos, responsables, tolerancia de stock, extras múltiples, autorizaciones y criterios de simultaneidad con el negocio. Medir tiempo de cotización y accesibilidad. Probar DDL, carga y restauración en MySQL antes de migrar. Los objetivos del APF1 mantienen su condición de metas del piloto.

Se verificaron además las once tablas y rechazo de una FK inexistente usando el DDL entregado en H2 modo MySQL, y la revocación de sesión por cambio de rol. H2 modo MySQL no sustituye una prueba en servidor MySQL real.
