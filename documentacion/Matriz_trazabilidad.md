# Matriz de trazabilidad

| Requisito | Módulo | Pantallas | Implementación | Verificación |
| --- | --- | --- | --- | --- |
| RF-01 | Autenticación | P01 | SecurityConfig / AccountStateFilter | anonymousRedirectAndRealLogin |
| RF-02 | Clientes | P03 | saveClient | csrfAndClientSubmission |
| RF-03 | Catálogo | P04 | saveItem / addComponent | invalidRecipeAndDiscountBlocked |
| RF-04 | Cotización | P05 P06 | createQuote | fullCycleAndHistoricalPrice |
| RF-05 | Documento | P07 | quote-detail.html | allAdminScreensRender + revisión visual |
| RF-06 | Aprobación | P07 P09 | approve | fullCycleAndHistoricalPrice / expiredApprovalBlocked |
| RF-07 | Calendario | P08 | WebController.events | allAdminScreensRender + revisión móvil |
| RF-08 | Plan del evento | P09 | saveEvent / addTask / toggleTask | eventCannotCloseWithBalance |
| RF-09 | Inventario | P10 | move | stockCannotBecomeNegative / movementAndRecipe |
| RF-10 | Requerimientos | P09 | requirements / expand | movementAndRecipe |
| RF-11 | Pagos | P11 | pay / voidPayment / balance | paymentRejectsOverpaymentAndFractionalCent |
| RF-12 | Alertas | P02 P12 | alerts | allAdminScreensRender |
| RF-13 | Reportes | P13 | reports / export | exportEscapesSpreadsheetFormulas |
| RF-14 | Auditoría | P15 | audit / LoginAudit | fullCycleAndHistoricalPrice |
| RF-15 | Usuarios y roles | P14 | saveUser / AccountStateFilter | cannotRemoveLastAdmin |

Cobertura de diseño: 15 de 15 requisitos funcionales tienen pantalla y componente asignado. Esta cobertura no equivale a aceptación empresarial ni a cumplimiento medido de todos los RNF.
