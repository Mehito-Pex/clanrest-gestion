# Especificación de requisitos de ClanRest Gestión

## Propósito y fuentes
El sistema interno centraliza el ciclo de cotización, coordinación de eventos, insumos y cobros. Conserva RF-01 a RF-14 del APF1 y añade RF-15 para explicitar la gestión de usuarios solicitada. La empresa se modela académicamente como catering en Piura. Visión, misión, diagnóstico y metas requieren validación; no se afirman entrevistas ni implementación empresarial.

## Usuarios y entorno
ADMIN administra el sistema; COORDINADOR gestiona clientes, propuestas y operación; ALMACEN consulta requerimientos, marca tareas y registra existencias. Los clientes no tienen cuenta. Java 21, Spring Boot 3.5.6, Maven, JPA, Thymeleaf, Bootstrap local y MySQL 8.0+; H2 para demostración y pruebas.

## Requisitos funcionales
| ID | Nombre | Especificación | Pantalla |
| --- | --- | --- | --- |
| RF-01 | Autenticación | Iniciar y cerrar sesión con permisos ADMIN, COORDINADOR y ALMACEN. | P01 |
| RF-02 | Clientes | Registrar, consultar, editar y desactivar clientes; registrar contactos, preferencias e historial. | P03 |
| RF-03 | Catálogo | Gestionar menús, platos, paquetes, extras e insumos; composición menú→plato→insumo por porción. | P04 |
| RF-04 | Cotización | Crear propuesta con cliente, asistentes, menú, extra opcional, vigencia, descuento en soles e impuesto configurable. | P05 P06 |
| RF-05 | Documento | Mostrar propuesta imprimible y permitir guardar PDF desde el navegador, con precios históricos. | P07 |
| RF-06 | Aprobación | Aprobar cotización vigente y generar un evento único; repetir aprobación devuelve el existente. | P07 P09 |
| RF-07 | Calendario | Mostrar calendario mensual y agenda filtrada por fechas, estado y responsable. | P08 |
| RF-08 | Plan del evento | Mantener horario, lugar, responsables, servicio, restricciones, personal, equipos, notas y tareas. | P09 |
| RF-09 | Inventario | Registrar entradas, salidas y ajustes firmados, motivo, usuario, evento opcional y stock resultante. | P10 |
| RF-10 | Requerimientos | Consolidar insumos por receta vigente y cantidad de asistentes, con stock y faltante. | P09 |
| RF-11 | Pagos | Registrar adelantos y pagos por evento; mostrar saldo; anular con motivo únicamente como ADMIN. | P11 |
| RF-12 | Alertas | Mostrar stock bajo, eventos a 7 días o atrasados, saldos pendientes y tareas vencidas. | P02 P12 |
| RF-13 | Reportes | Filtrar ventas comprometidas, cobros, saldos, cotizaciones, eventos y movimientos; exportar CSV de eventos. | P13 |
| RF-14 | Auditoría | Registrar actor, fecha, acción, entidad e ID en operaciones críticas e inicio de sesión. | P15 |
| RF-15 | Usuarios y roles | Crear y actualizar usuarios, contraseñas y roles; conservar un ADMIN activo y revocar permisos obsoletos. | P14 |

## Requisitos no funcionales y verificación
| ID | Atributo | Criterio | Estado |
| --- | --- | --- | --- |
| RNF-01 | Usabilidad | Cotización estándar en ≤5 minutos por usuario capacitado. | Pendiente prueba con usuarios; etiquetas, navegación y formularios implementados. |
| RNF-02 | Rendimiento | 95% de operaciones comunes en <3 segundos bajo carga piloto acordada. | Pendiente prueba de carga; no se afirma medición de SLA. |
| RNF-03 | Seguridad | BCrypt, CSRF, roles en rutas y servicios, rechazo de permisos obsoletos. | Implementado; pruebas de login, CSRF y denegación por rol. |
| RNF-04 | Disponibilidad | Objetivo 99% en horario operativo, salvo mantenimiento. | Meta de despliegue; ejecución local no demuestra disponibilidad. |
| RNF-05 | Respaldo | Copia diaria y restauración mensual en entorno de prueba. | Procedimiento documentado; programación operativa pendiente. |
| RNF-06 | Compatibilidad | Interfaz adaptable a escritorio y teléfono. | Capturas en Chrome a 1440 y 390 px; otros navegadores pendientes. |
| RNF-07 | Mantenibilidad | Capas, repositorios JPA, pruebas de reglas críticas y README. | Implementado; una clase de servicio concentra reglas para el piloto. |
| RNF-08 | Privacidad | Datos mínimos, cuentas por rol y dataset ficticio. | Implementado en piloto; validación empresarial y política de retención pendientes. |
| RNF-09 | Integridad | Transacciones en aprobación, pagos y stock; versión y bloqueo de registros. | Implementado; pruebas de duplicidad, sobrepago y stock insuficiente. |
| RNF-10 | Accesibilidad | Etiquetas, teclado, mensajes legibles y contraste. | Etiquetas y HTML semántico implementados; auditoría de accesibilidad pendiente. |

## Reglas de negocio
1. Cada propuesta tiene código único, cliente, vigencia, fecha/hora futura, lugar y entre 1 y 10000 asistentes.
2. Subtotal = suma de cantidad × precio histórico. Total = (subtotal − descuento en soles) × (1 + impuesto/100), redondeado a dos decimales con HALF_UP.
3. El descuento no puede superar el subtotal; impuesto entre 0 y 100. La propuesta admite un menú/paquete y un extra opcional en el primer incremento.
4. Solo BORRADOR o ENVIADA vigente puede aprobarse; aprobación y evento se guardan en una transacción con unicidad sobre quotation_id.
5. Los precios de QuoteLine y total aprobado permanecen históricos. La receta del cálculo operativo es la vigente; los movimientos guardan el consumo real.
6. El calendario permite servicios simultáneos; la capacidad de equipos se coordina por responsables, sin asignación automática de recursos.
7. Estados del evento: CONFIRMADO → EN_PREPARACION → EN_SERVICIO → CERRADO. Cancelación desde confirmado/preparación; cierre exige saldo cero.
8. Cada movimiento de stock indica tipo, cantidad, motivo, usuario y existencia resultante. AJUSTE es delta firmado; solo ADMIN puede autorizar negativo.
9. Un pago debe ser positivo, con máximo dos decimales, sin exceder el saldo y con fecha no futura; eventos cancelados no aceptan pagos.
10. Anular pago requiere ADMIN y motivo. Se conserva el registro y se excluye del total pagado. Un evento cerrado vuelve a EN_SERVICIO si se anula un cobro.
11. Menú y paquete contienen platos; plato contiene insumos. Esta estructura impide ciclos de recetas.
12. Las cuentas usan BCrypt, no se muestran contraseñas ni hashes, y el último ADMIN activo no puede desactivarse o degradarse.

## Levantamiento que debe ejecutarse
Entrevista al administrador sobre precios, aprobaciones, saldos y descuentos; observación de un servicio con autorización; revisión de formatos anonimizados; taller de prototipos con coordinación y almacén. Para cada sesión, registrar fecha, participante, requisito confirmado, evidencia y cambio aprobado. No se dispone aún de evidencias de campo. Validar especialmente impuesto, estructura de recetas, capacidad simultánea, retención y necesidad de varios extras por cotización.

## Fuera de alcance
Contabilidad y planillas, geolocalización, marketplace, fijación automática por IA, SUNAT, pagos en línea y portal de clientes. Tampoco se implementan devoluciones bancarias automáticas, costos/rentabilidad contable, reservas automáticas de inventario ni motor de ejecución BPMN.

## Aceptación del piloto
El equipo demuestra cliente → menú/receta → cotización → aprobación → evento → movimiento → pago → reporte. El docente y representantes validan ≥80% de escenarios acordados. Las metas de negocio del APF1 requieren línea base de dos semanas y pruebas con usuarios; los resultados automáticos no sustituyen esa validación.
