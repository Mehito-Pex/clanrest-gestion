# Diseño UX UI y prototipos

## Herramientas y archivos
Mockups editables: prototipos/ClanRest_Mockups.drawio, compatible con diagrams.net, una herramienta gráfica de diagramación que permite construir wireframes. No se utilizó Balsamiq. Se entregan 15 pantallas para la solución elegida y 5 para cada alternativa. Los PNG presentan cada pantalla; index.html permite recorrerlas y simular el flujo cotización → evento → pago. El prototipo conceptual no guarda datos. Las capturas de documentacion/capturas corresponden al aplicativo ejecutado.

## Cobertura
| Pantalla | Requisitos |
| --- | --- |
| P01 | Autenticación y cierre de sesión |
| P02 | Indicadores y navegación |
| P03 | Clientes y preferencias |
| P04 | Catálogo y recetas |
| P05 | Lista de propuestas |
| P06 | Cálculo y captura de alcance |
| P07 | Aprobación e impresión |
| P08 | Agenda y filtros |
| P09 | Plan de evento y requerimientos |
| P10 | Stock y movimientos |
| P11 | Cobros y anulaciones |
| P12 | Seguimiento oportuno |
| P13 | Reportes operativos y comerciales |
| P14 | Administración de accesos |
| P15 | Trazabilidad |

## Criterios de interacción
Navegación persistente y acceso según rol, etiquetas visibles, campos agrupados por propósito, estados explícitos y mensajes de rechazo. ADMIN controla accesos y anulaciones; ALMACEN recibe tareas e insumos; COORDINADOR mantiene el flujo comercial. El formulario conserva precios al crear cada versión y evita descuentos inválidos. Tabla para datos, calendario para agenda y CSV para análisis externo. Adaptación responsive mediante Bootstrap y estilos locales.

## Validación propuesta
Usuario coordinador: registrar propuesta en ≤5 minutos. Almacén: identificar faltante y registrar movimiento sin ayuda. Administrador: revisar saldo, anular registro incorrecto y consultar auditoría. Registrar éxito, tiempo, errores y cambio solicitado. La cobertura de pantallas es completa respecto de la SRS; usabilidad y accesibilidad necesitan pruebas con participantes.
