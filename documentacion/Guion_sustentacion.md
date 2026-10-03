# Guion de sustentación de 23 minutos

## Reparto sugerido sujeto al acuerdo del equipo
Juan Abel: contexto y alternativas, 6 minutos. Hector Zenon: procesos y diseño de datos, 7 minutos. Carlos Daniel: interfaz, demo y validación, 8 minutos. Cierre compartido: 2 minutos. Todos participan; límite grupal aproximado 25 minutos según consigna.

1. Contexto (3 min). Explicar catering y dispersión de información; distinguir supuestos de observación real. Relacionar misión de atención personalizada con información centralizada y presentar Canvas.
2. Alternativas (3 min). Comparar web, móvil y CRM con cinco pantallas mínimas y objetivos Java. Justificar web integral por uso interno, esfuerzo y trazabilidad.
3. BPMN (3 min). Mostrar AS-IS y TO-BE. Identificar círculo, tarea, XOR, flecha y carril. Recorrer faltante de pollo y ciclo de compra/entrada. El BPMN es descriptivo, no motor automatizado.
4. Clases y BD (4 min). Explicar QuoteLine histórico, relación única cotización-evento, menú/plato/insumo, pagos anulados e integridad por transacciones y bloqueos. Mostrar DDL y permisos mínimos.
5. Prototipo y UX (3 min). Abrir index.html o .drawio; presentar cobertura de 15 requisitos, restricciones por rol y adaptación móvil.
6. Demo (5 min). Login; crear propuesta 100×35 −100 con 18% = S/4012; aprobar; revisar receta; registrar entrada/salida; cobrar y consultar reporte/auditoría. Usar cuentas por rol si el tiempo lo permite.
7. Evidencia y cierre (2 min). Mostrar suite aprobada y límites pendientes. Explicar cómo validar indicadores con línea base real. Reconocer que no se midió disponibilidad ni se probó servidor MySQL.

## Preguntas para practicar
¿Por qué una tabla de detalle? Conserva cada precio/cantidad de la propuesta. ¿Por qué BigDecimal? Evita errores de representación binaria del dinero. ¿Cómo evitar doble evento? Bloqueo de cotización y FK única. ¿Qué representa XOR? Una sola salida según condición. ¿Por qué anular y no borrar? Conserva historia. ¿Se demostró impacto empresarial? Aún no; requiere mediciones y usuarios. ¿Puede cerrar con saldo? No. ¿La receta queda congelada? No, requerimiento vigente; consumo real queda en movimientos.
