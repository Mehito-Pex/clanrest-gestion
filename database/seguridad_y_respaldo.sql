-- Ejecutar como administrador de MySQL, reemplazando la contraseña.
-- No utilizar root desde el aplicativo.
CREATE USER 'clanrest'@'localhost' IDENTIFIED BY 'REEMPLAZAR_CON_CLAVE_PROPIA';
GRANT SELECT, INSERT, UPDATE ON clanrest.* TO 'clanrest'@'localhost';
-- La edición de recetas utiliza DELETE únicamente sobre components.
GRANT DELETE ON clanrest.components TO 'clanrest'@'localhost';
-- El usuario de ejecución no recibe CREATE, DROP ni ALTER.
-- Respaldo (terminal): mysqldump -u usuario_respaldo -p --single-transaction clanrest > respaldo.sql
-- Restauración en una BD de prueba: mysql -u administrador -p clanrest_prueba < respaldo.sql
