# ClanRest Gestión

Proyecto académico para administrar la operación de una empresa de catering. El sistema controla clientes, catálogo, cotizaciones, eventos, inventario, pagos, alertas, reportes, usuarios y auditoría.

## Tecnologías

- Java 21 y Spring Boot 3.5.6
- Maven y Spring Data JPA
- Thymeleaf y Bootstrap
- Spring Security
- H2 para demostración y MySQL como alternativa
- JUnit, TDD y JaCoCo
- Guava, Apache POI, Apache Commons y Logback

## Proyecto

Requisitos: JDK 21 y Maven 3.9 o superior.

```powershell
git clone https://github.com/Mehito-Pex/clanrest-gestion.git
cd clanrest-gestion
mvn clean test
mvn spring-boot:run
```



## Cuentas de demostración

| Usuario | Rol | Contraseña |
| --- | --- | --- |
| admin | ADMIN | ClanRest2026! |
| coordinador | COORDINADOR | ClanRest2026! |
| almacen | ALMACEN | ClanRest2026! |

## Módulos principales

1. Autenticación y autorización por roles.
2. Clientes y catálogo con recetas.
3. Cotizaciones, aprobación y conversión a evento.
4. Calendario, tareas y requerimientos del evento.
5. Inventario y movimientos de stock.
6. Pagos, saldos y anulaciones.
7. Alertas y reportes con exportación CSV y Excel.
8. Usuarios, roles y auditoría.

## Estructura del repositorio

```text
clanrest-gestion/
├── src/main/java/          Código Java
├── src/main/resources/     Pantallas y configuración
├── src/test/               Pruebas automatizadas
├── database/               Esquema MySQL
├── documentacion/avance3/  Informe, presentación y guía
├── pom.xml                 Dependencias Maven
└── README.md               Ejecución y descripción
```

## Evidencia técnica

- 20 pruebas automatizadas aprobadas.
- 82.8 % de cobertura de instrucciones con JaCoCo.
- Arquitectura MVC con servicios y repositorios DAO.
- Seguridad con BCrypt, CSRF, validaciones y control por roles.
- Exportación Excel implementada en `ExcelReportService`.
