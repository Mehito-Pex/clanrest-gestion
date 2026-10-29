# Matriz de cobertura del Avance 3

| Alcance comprometido | Interfaz o flujo | Código principal | Prueba o evidencia | Estado |
| --- | --- | --- | --- | --- |
| Autenticación | Login y cierre de sesión | SecurityConfig | `anonymousRedirectAndRealLogin` | Completo |
| Panel principal | Indicadores, agenda y alertas | WebController y home.html | `allAdminScreensRender` | Completo |
| Clientes | Lista, registro, edición e historial | Client, ClientRepository y BusinessService | `csrfAndClientSubmission` | Completo |
| Catálogo | Menús, platos, paquetes, extras e insumos | CatalogItemRepository | Pruebas de pantallas y reglas | Completo |
| Recetas | Composición de menús y platos | ComponentRepository | Ciclo de negocio | Completo |
| Cotizaciones | Creación, estados y propuesta | Quotation y QuoteLine | `fullCycleAndHistoricalPrice` | Completo |
| Aprobación | Conversión idempotente a evento | BusinessService.approve | `fullCycleAndHistoricalPrice` | Completo |
| Calendario | Vista y filtro de eventos | events.html | `allAdminScreensRender` | Completo |
| Evento | Ficha, tareas y requerimientos | Event y EventTask | Pruebas de flujo | Completo |
| Inventario | Entradas, salidas y ajustes | StockMovement | Pruebas de roles y cantidades | Completo |
| Pagos | Registro, saldo y anulación | Payment | `fullCycleAndHistoricalPrice` | Completo |
| Alertas | Stock, agenda y saldos | BusinessService.alerts | Prueba de pantalla | Completo |
| Reportes | Indicadores, CSV, impresión y Excel | ExcelReportService | 3 pruebas unitarias y 1 de integración | Completo |
| Usuarios y roles | Alta, edición y revocación de sesión | AppUser y AccountStateFilter | `changedRoleRevokesOldSessionOnNextRequest` | Completo |
| Auditoría | Registro de operaciones críticas | AuditEntryRepository | Verificación de anulación | Completo |

La implementación dispone de interfaz para los quince componentes del alcance funcional. Las limitaciones académicas siguen siendo la validación con usuarios reales, pruebas de carga, servidor MySQL operativo y restauración de respaldos.
