# Guion de sustentación del Avance 3

Duración objetivo: 9 minutos y 30 segundos. El tiempo restante permite responder una pregunta breve.

## Integrante 1  Contexto y arquitectura  3 minutos

Presentar el problema que resuelve ClanRest y el alcance. Explicar el recorrido navegador, controlador, servicio, repositorio DAO y base de datos. Relacionar las capas con MVC. Mencionar cómo Spring inyecta dependencias y cómo cada servicio conserva una responsabilidad específica.

## Integrante 2  Implementación y seguridad  3 minutos

Demostrar clientes, cotizaciones y conversión a evento. Abrir Reportes y descargar Excel. Explicar Apache POI, Guava, Apache Commons y Logback. Mostrar los roles, BCrypt, CSRF, validaciones y la neutralización de fórmulas.

## Integrante 3  Pruebas y GitHub  3 minutos y 30 segundos

Mostrar el archivo RED donde el servicio aún no existe y el resultado GREEN con tres pruebas aprobadas. Ejecutar o enseñar el resultado final de 20 pruebas. Abrir el historial de GitHub y explicar que cada cambio del Avance 3 tiene código, pruebas y documentación versionados. Cerrar con la matriz de cobertura.

## Respuestas breves

**Por qué usar DAO:** evita SQL en el controlador y permite cambiar H2 por MySQL sin reescribir las reglas.

**Dónde se aplica SOLID:** el servicio Excel tiene una sola responsabilidad y se inyecta en el controlador. Los repositorios separan contratos por entidad.

**Qué demuestra TDD:** las pruebas definieron el comportamiento antes de crear el servicio. El resultado RED registra la ausencia de la clase y GREEN confirma la implementación.

**Por qué cuatro librerías:** la rúbrica pide todas para el máximo puntaje. Cada una participa en una función visible y comprobable.

**Qué falta para producción:** pruebas con usuarios, HTTPS, secretos externos, MySQL desplegado, carga y restauración de respaldo.
