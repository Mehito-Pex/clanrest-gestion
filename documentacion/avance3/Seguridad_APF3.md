# Seguridad del Avance 3

| Control | Implementación | Verificación |
| --- | --- | --- |
| Autenticación | Formulario de Spring Security | Prueba de acceso real |
| Autorización | Rutas y métodos restringidos por ADMIN, COORDINADOR y ALMACEN | Pruebas de acceso permitido y denegado |
| Contraseñas | BCrypt | Configuración `PasswordEncoder` |
| CSRF | Token obligatorio en formularios POST | Prueba `csrfAndClientSubmission` |
| Sesión | Expira a los 30 minutos y valida el estado de la cuenta | `AccountStateFilter` |
| Entrada | Reglas de longitud, formato, estados y cantidades | Pruebas de negocio e integración |
| Errores | Mensajes controlados y sin trazas para el usuario | `ErrorAdvice` y configuración |
| Base de datos | JPA parametriza las consultas y el script MySQL define permisos mínimos | Repositorios y SQL de seguridad |
| Exportación | Límite de 10 000 filas y neutralización de fórmulas | `ExcelReportServiceTest` |
| Registro | Logback registra eventos técnicos sin contraseñas ni contenido personal | `logback-spring.xml` |

Para un despliegue real se deben reemplazar las cuentas demo, desactivar la carga de datos ficticios, configurar HTTPS, usar secretos externos y ejecutar pruebas de respaldo y restauración.
