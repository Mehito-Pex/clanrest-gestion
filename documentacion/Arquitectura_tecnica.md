# Documentación técnica

## Arquitectura
Navegador → Spring Security → WebController → BusinessService → repositorios Spring Data JPA → Hibernate → H2/MySQL. Thymeleaf genera HTML desde el servidor; esta versión no expone una API REST pública. Bootstrap se sirve localmente, sin dependencia de CDN en ejecución. Entidades JPA con @Version; PESSIMISTIC_WRITE al modificar stock, aprobar y registrar pagos. Las reglas críticas viven en métodos @Transactional con @PreAuthorize. Formularios POST incluyen CSRF a través de Thymeleaf.

## Componentes
entity: once clases de persistencia. repository: once interfaces JpaRepository. service/BusinessService: reglas, cálculo, alertas y auditoría. controller/WebController: rutas y formularios. ErrorAdvice: errores de dominio sin detalles internos. config/SecurityConfig: login y roles. AccountStateFilter: invalida sesiones con cuentas inactivas o rol distinto. DemoData: dataset inicial idempotente cuando no existen usuarios. LoginAudit: registro de autenticación exitosa.

## Contratos principales
| Método | Ruta | Comportamiento |
| --- | --- | --- |
| GET | /clients /catalog /quotes /events /inventory /payments | Consultar módulos según permisos |
| POST | /quotes/save | Validar datos y guardar propuesta calculada |
| POST | /quotes/{id}/approve | Aprobar y convertir sin duplicados |
| POST | /events/{id}/save | Validar transición y actualizar plan |
| POST | /inventory/move | Bloquear artículo, registrar delta y auditoría |
| POST | /payments/save | Bloquear evento y evitar sobrepago |
| POST | /payments/{id}/void | ADMIN conserva registro con anulación |
| GET | /reports/export | CSV escapado contra fórmulas de hoja de cálculo |
| POST | /users/save | ADMIN cambia cuentas con protección del último administrador |

## Datos, cálculo y tiempo
Dinero: BigDecimal, 2 decimales HALF_UP; cantidades de stock: 3 decimales. Fechas y horas son locales al negocio; al desplegar configurar zona America/Lima. Los montos del reporte son ventas comprometidas, no reconocimiento contable. La fecha de evento determina filtros de ventas/cobros; cotización usa fecha de creación; stock usa fecha de movimiento. Receta vigente para requerimientos, cantidades reales en movimientos. Los campos staff/equipment son texto descriptivo; no existe optimización automática de personal.

## Seguridad y operación
Escucha en 127.0.0.1 por defecto. Para exposición pública se requiere proxy HTTPS, secretos propios, datos no demo, respaldo y validación de controles. La auditoría es inmutable desde la UI, no es evidencia criptográficamente inviolable ante un administrador de BD. Logout y cambios de rol usan las comprobaciones de cuenta; un cambio de contraseña no invalida por sí solo una sesión ya autenticada. Para desarrollo, sin consola H2 pública. No hay eliminación de históricos desde la interfaz; solo se eliminan componentes de recetas.

## Límites del piloto
Un menú/paquete y un extra opcional por cotización; múltiples componentes por receta. No calcula rentabilidad de costos reales ni genera factura SUNAT. La impresión PDF usa el navegador. Los reportes no ejecutan cobros digitales ni devoluciones. Las pruebas automáticas usan H2; no equivalen a prueba de carga, aceptación empresarial o validación MySQL. Es una base académica funcional ampliable por módulos.
