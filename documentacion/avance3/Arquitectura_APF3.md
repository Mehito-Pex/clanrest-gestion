# Arquitectura del Avance 3

## Flujo principal

```text
Navegador
   |
   v
Vistas Thymeleaf y Bootstrap
   |
   v
WebController
   |
   +----> BusinessService
   |          |
   |          v
   |     Repositorios Spring Data JPA
   |          |
   |          v
   |       H2 o MySQL
   |
   +----> ExcelReportService
              |
              v
        Archivo XLSX con Apache POI
```

## MVC

Las entidades JPA representan el modelo persistente. `WebController` recibe solicitudes HTTP, valida el acceso mediante Spring Security y coordina la respuesta. Las plantillas Thymeleaf constituyen las vistas. Las reglas del negocio permanecen en `BusinessService` y la generación de Excel en `ExcelReportService`.

## DAO

Las once interfaces de `repository` extienden `JpaRepository`. Cada repositorio encapsula el acceso a datos y evita que los controladores ejecuten SQL. El perfil predeterminado utiliza H2 y el perfil `mysql` conserva los mismos contratos DAO.

## TDD

La exportación Excel comenzó con tres pruebas que no compilaban porque `ExcelReportService` todavía no existía. Luego se implementó el mínimo código para generar el libro, neutralizar fórmulas y limitar el volumen. Las tres pruebas unitarias y las diecisiete pruebas de integración terminaron aprobadas.

## SOLID

- Responsabilidad única: `ExcelReportService` solo genera el archivo y no consulta la base de datos ni atiende HTTP.
- Inversión de dependencias: Spring inyecta servicios y repositorios por constructor.
- Abierto y cerrado: la exportación XLSX se añadió sin modificar el contrato de los repositorios ni las entidades.
- Segregación de interfaces: cada repositorio representa un agregado específico.
- Sustitución: H2 y MySQL pueden reemplazarse mediante el perfil de configuración sin cambiar la lógica de negocio.

## Seguridad

Spring Security protege las rutas por rol. Los formularios POST usan CSRF, las contraseñas se almacenan con BCrypt y los mensajes de error no exponen trazas. La exportación acepta únicamente filas ya autorizadas, limita el libro a 10 000 registros y convierte textos que empiezan con `=`, `+`, `-` o `@` en valores seguros.
