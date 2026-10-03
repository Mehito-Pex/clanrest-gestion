# Diseño lógico y físico de la base de datos

## Modelo lógico

El dominio tiene once tablas. Clientes a cotizaciones es 1:N; cotizaciones a líneas es 1:N; cotización a evento es 1:0..1 con clave única; evento a pagos y tareas es 1:N. Un artículo se referencia en líneas, movimientos y en ambas posiciones de una composición. Evento de un movimiento es opcional. Usuarios y auditoría son entidades transversales; actor se conserva como texto histórico y no depende de una cuenta que pueda desactivarse.

Las tablas separan cabecera y detalle y evitan repetir contacto en líneas. Los nombres y precios de líneas son copias históricas deliberadas; stock y total son agregados mantenidos por transacciones. Se distinguen estas decisiones de una normalización puramente teórica.

## Diccionario físico

Todas las tablas tienen id BIGINT AUTO_INCREMENT y version BIGINT para concurrencia optimista.

### clients

| Campo | Tipo y restricción |
| --- | --- |
| name | VARCHAR(120) NOT NULL |
| document_number | VARCHAR(255) |
| phone | VARCHAR(255) |
| email | VARCHAR(255) |
| preferences | VARCHAR(1000) |
| active | BIT NOT NULL |

### catalog_items

| Campo | Tipo y restricción |
| --- | --- |
| name | VARCHAR(255) NOT NULL |
| kind | VARCHAR(255) NOT NULL |
| unit | VARCHAR(255) |
| price | DECIMAL(12,2) NOT NULL |
| stock | DECIMAL(12,3) NOT NULL |
| minimum | DECIMAL(12,3) NOT NULL |
| description | VARCHAR(1000) |
| active | BIT NOT NULL |

### components

| Campo | Tipo y restricción |
| --- | --- |
| parent_id | BIGINT NOT NULL |
| ingredient_id | BIGINT NOT NULL |
| quantity | DECIMAL(12,3) NOT NULL |

### quotations

| Campo | Tipo y restricción |
| --- | --- |
| client_id | BIGINT NOT NULL |
| code | VARCHAR(255) UNIQUE |
| created | DATE |
| valid_until | DATE |
| event_date | DATETIME(6) |
| attendees | INT NOT NULL |
| location | VARCHAR(255) |
| restrictions | VARCHAR(1000) |
| status | VARCHAR(255) |
| discount | DECIMAL(12,2) |
| tax_percent | DECIMAL(5,2) |
| total | DECIMAL(12,2) |

### quote_lines

| Campo | Tipo y restricción |
| --- | --- |
| quotation_id | BIGINT NOT NULL |
| item_id | BIGINT NOT NULL |
| description | VARCHAR(255) |
| quantity | INT NOT NULL |
| unit_price | DECIMAL(12,2) |

### events

| Campo | Tipo y restricción |
| --- | --- |
| quotation_id | BIGINT NOT NULL UNIQUE |
| date | DATETIME(6) |
| location | VARCHAR(255) |
| responsible | VARCHAR(255) |
| status | VARCHAR(255) |
| service | VARCHAR(1000) |
| staff | VARCHAR(1000) |
| equipment | VARCHAR(1000) |
| notes | VARCHAR(2000) |

### event_tasks

| Campo | Tipo y restricción |
| --- | --- |
| event_id | BIGINT NOT NULL |
| description | VARCHAR(255) |
| responsible | VARCHAR(255) |
| due_date | DATE |
| done | BIT NOT NULL |

### stock_movements

| Campo | Tipo y restricción |
| --- | --- |
| item_id | BIGINT NOT NULL |
| event_id | BIGINT |
| kind | VARCHAR(255) |
| quantity | DECIMAL(12,3) |
| resulting_stock | DECIMAL(12,3) |
| reason | VARCHAR(255) |
| actor | VARCHAR(255) |
| timestamp | DATETIME(6) |

### payments

| Campo | Tipo y restricción |
| --- | --- |
| event_id | BIGINT NOT NULL |
| amount | DECIMAL(12,2) |
| date | DATE |
| method | VARCHAR(255) |
| reference | VARCHAR(255) |
| voided | BIT NOT NULL |
| void_reason | VARCHAR(255) |
| actor | VARCHAR(255) |

### app_users

| Campo | Tipo y restricción |
| --- | --- |
| username | VARCHAR(255) NOT NULL UNIQUE |
| password | VARCHAR(255) NOT NULL |
| full_name | VARCHAR(255) |
| role | VARCHAR(255) NOT NULL |
| active | BIT NOT NULL |

### audit_entries

| Campo | Tipo y restricción |
| --- | --- |
| timestamp | DATETIME(6) |
| actor | VARCHAR(255) |
| action | VARCHAR(255) |
| entity_name | VARCHAR(255) |
| entity_id | BIGINT |
| detail | VARCHAR(2000) |

## Integridad, seguridad y operación

DDL en database/clanrest.sql. Índices en fecha del evento, estado de propuesta y fechas de auditoría/movimientos; claves foráneas indexadas por InnoDB. DELETE RESTRICT protege históricos. No se almacenan contraseñas en texto; el hash BCrypt se guarda en app_users.password. Parámetros de BD mediante variables de entorno. El perfil MySQL usa validate y no modifica estructura. Un administrador aplica DDL; la cuenta de aplicación no requiere privilegios DDL.

El perfil H2 crea/actualiza estructura para demostración local. MySQL no estuvo disponible para validar un servidor real; su DDL y perfil son entregables de migración que deben probarse antes del uso empresarial. Respaldar diariamente y restaurar mensualmente en una BD separada; para H2 copiar el archivo solo con la aplicación detenida.
