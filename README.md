# ClanRest Gestión

## Avance de Proyecto Final 3

El Avance 3 añade exportación de reportes a Excel, evidencia TDD, las cuatro librerías Java solicitadas, Logback, JaCoCo, Javadoc y documentación de sustentación. La entrega se encuentra en `documentacion/avance3/`. La verificación actual ejecuta 20 pruebas sin fallos.

Proyecto académico funcional del Avance 2, basado en Clan_Rest_Catering_APF1.docx y APF2_INDICACIONES Y RUBRICA _T2_LAJZM.pdf. Incluye Java 21, Spring Boot 3.5.6, Maven, Spring Data JPA, Thymeleaf/Bootstrap, MySQL y H2. Usa datos ficticios y no presupone validación empresarial.

## Uso desde GitHub
El repositorio contiene el código fuente, las pruebas, diagramas, mockups, documentos y una verificación automática en `.github/workflows/ci.yml`. Al clonarlo, instala JDK 21 y Maven 3.9+ y sigue **Ejecutar desde el código**. El JAR, Java portable, Maven portable y la base de datos local pertenecen solo al paquete de entrega; se excluyen del control de versiones. Cada colaborador genera su propio JAR con `mvn package`.

## Ejecutar en Windows con el paquete portable
En la entrega completa se incluyen runtime/ y clanrest-1.0.0.jar. Ejecutar INICIAR_CLANREST.cmd; abrir http://127.0.0.1:8080. No reemplaza el Java instalado. Los datos H2 se guardan en data/ junto al lanzador; detener con Ctrl+C y reiniciar conserva los registros. El puerto 8080 debe estar libre.

## Ejecutar desde el código
Instalar/configurar JDK 21 y Maven 3.9+ y comprobar java -version / mvn -version. Desde la carpeta del proyecto:
```powershell
mvn test
mvn package
java "-Duser.timezone=America/Lima" -jar target/clanrest-1.0.0.jar
```
Abrir http://127.0.0.1:8080. Para trabajar desde IDE, importar pom.xml y seleccionar JDK 21. La primera compilación necesita internet para las dependencias; el JAR empaquetado y Bootstrap local no necesitan internet.

## Cuentas demo
| Usuario | Rol | Contraseña |
| --- | --- | --- |
| admin | ADMIN | ClanRest2026! |
| coordinador | COORDINADOR | ClanRest2026! |
| almacen | ALMACEN | ClanRest2026! |

La carga inicial se ejecuta si no hay usuarios y clanrest.demo=true. Para reiniciar demo, detener aplicación y guardar o retirar data/clanrest.mv.db. No borrar mientras esté en ejecución.

## MySQL 8.0 o superior
Crear estructura ejecutando database/clanrest.sql como administrador. Crear cuenta de permisos mínimos siguiendo seguridad_y_respaldo.sql. Configurar en PowerShell:
```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/clanrest?serverTimezone=America/Lima'
$env:DB_USER='clanrest'
$env:DB_PASSWORD='tu_clave_propia'
$env:DEMO_DATA='true'
java "-Duser.timezone=America/Lima" -jar target/clanrest-1.0.0.jar --spring.profiles.active=mysql
```
DEMO_DATA=true crea cuentas y dataset inicial si no hay usuarios. No ejecutar datos_prueba.sql además de DemoData. Para datos propios, cambiar contraseñas, desactivar demo y sustituir registros ficticios. El perfil MySQL valida esquema; el perfil H2 crea/actualiza esquema. MySQL no estuvo disponible en el entorno de verificación y requiere prueba de migración local.

## Módulos y demostración
Clientes; catálogo con menús, paquetes, platos, extras e insumos; recetas; propuestas imprimibles; aprobación/conversión; calendario; plan/tareas de eventos; requerimientos; stock/movimientos; pagos/saldos/anulaciones; alertas; reportes/CSV; usuarios/roles; auditoría. Ver Manual_usuario.md para recorrido.

## Artefactos de diseño
- documentacion/: SRS, matriz, Charter, diseño BD, BPMN, UX/UI, arquitectura, manual, rúbrica, validación y guion.
- diagramas/: BPMN 2.0 XML, PNG, .drawio, UML, ER y Gantt.
- prototipos/: ClanRest_Mockups.drawio con 25 páginas y index.html navegable; 15 pantallas elegidas y 5 por cada otra alternativa.
- documentacion/capturas/: evidencia del aplicativo real.
- database/: DDL, datos de dominio opcionales y permisos/respaldo.

## Límites y verificación
16 pruebas de integración aprobadas sobre H2. Cobertura de pantallas del alcance SRS: 15/15 requisitos funcionales. Pendientes: aceptación empresarial, metas de negocio, carga, respaldo/restauración y servidor MySQL. Una propuesta contiene un menú/paquete y un extra opcional; recetas admiten varios componentes. Recetas vigentes para requerimientos; precios de propuestas son históricos. No hay contabilidad, SUNAT, cobro online ni portal público. Consultar SRS y arquitectura.

## Respaldo y solución de problemas
H2: detener aplicativo y copiar data/clanrest.mv.db a ubicación segura; restaurar copia con aplicativo detenido. MySQL: copia diaria y restauración de prueba según instrucciones SQL. Si aparece puerto ocupado: usar --server.port=8081. Si se usa Java 8: ejecutar con JDK 21 o lanzador portable. Si un formulario se rechaza: volver y revisar mensaje/campos. El sistema escucha solo en localhost por defecto.

## Referencias técnicas
Spring Boot: https://docs.spring.io/spring-boot/3.5/system-requirements.html
Spring Security: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html
Thymeleaf: https://www.thymeleaf.org/documentation
BPMN: https://www.omg.org/spec/BPMN/2.0.2/
Bootstrap tiene licencia MIT incluida en static/vendor/. Runtime Temurin mantiene sus archivos de licencia. El aplicativo es una base académica y necesita revisión antes de exposición pública.

## Compilar sin instalar Maven globalmente
La entrega completa incluye tools/maven y JDK21 en runtime. Ejecutar COMPILAR_Y_PROBAR.cmd para descargar dependencias, verificar y generar el JAR; requiere internet la primera vez. El código medido supera 50% Java por líneas y bytes: consultar documentacion/Participacion_Java.md.


## Informe y sustentación
El Word final está en documentacion/Avance_2.docx. La presentación de 15 diapositivas está en documentacion/Sustentacion_ClanRest.pptx y su equivalente navegable en documentacion/Presentacion_ClanRest.html. El guion propone 23 minutos de exposición.
