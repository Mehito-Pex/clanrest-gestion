# Modelado de procesos BPMN

## Alcance y notación
Los archivos BPMN_AS_IS.bpmn y BPMN_TO_BE.bpmn modelan procesos descriptivos no ejecutables con BPMN 2.0. Los PNG permiten lectura; los .drawio y .bpmn permiten edición. Un círculo de borde simple inicia, un círculo grueso termina, rectángulo redondeado representa tarea y rombo representa decisión exclusiva XOR. Flecha continua indica secuencia; carriles distribuyen responsabilidades dentro del proceso. Atención y cliente representa la interacción atendida por la empresa; no modela al cliente como participante externo independiente. No se utilizan flujos de mensaje entre carriles. La notación sigue la especificación OMG (2014).

## AS IS
El diagnóstico propuesto en APF1 supone recepción por chat, cálculo en archivo, aceptación, anotación de reserva y adelanto, revisión manual de insumos, preparación/servicio y cobro final. Si el cliente no acepta, se archiva la solicitud. El flujo revela tres puntos de dispersión: cálculo, existencia física y registro del cobro. Es una hipótesis de trabajo que debe confirmarse observando el negocio.

## TO BE
Registrar cliente y restricciones; calcular propuesta con precios históricos; verificar aceptación y vigencia; aprobar y convertir atómicamente a un único evento; consolidar insumos y comparar con stock; ante faltante, registrar compra/entrada y volver a revisar; registrar salidas reales y ejecutar el servicio; registrar pagos y cerrar cuando el saldo sea cero. El XOR de stock toma una sola salida. El aplicativo no ejecuta automáticamente el servicio físico ni envía mensajes al cliente.

## Ejemplo de recorrido
Cincuenta asistentes consumen 6 kg de arroz y 10 kg de pollo según la receta demo. Con 5 kg de pollo, faltan 5 kg: se registra una entrada y luego la salida vinculada al evento. El menú de S/35 da total S/1750 sin impuesto ni descuento. Un adelanto de S/500 deja S/1250. Tras registrar el saldo y avanzar estados se puede cerrar el evento. Si una propuesta vence, la aprobación se rechaza y se crea una nueva versión.

## Indicadores y validación
Comparar tiempo de cotización, fichas completas y diferencias de inventario antes/después. No se dispone de mediciones empresariales. El TO-BE guía la demostración y la matriz, mientras la validación con usuarios debe confirmar pasos y responsables.
