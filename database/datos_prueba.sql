-- Dataset de dominio opcional para MySQL. Ejecutar una sola vez en tablas vacías.
-- Para crear además las tres cuentas demo con BCrypt, iniciar con DEMO_DATA=true
-- ANTES de ejecutar este script; DemoData ya crea un dataset equivalente, por lo
-- que no debe combinarse su carga con este SQL. En modo demo NO ejecutar este archivo.
INSERT INTO clients(id,version,name,document_number,phone,email,preferences,active) VALUES(1,0,'Eventos del Norte Demo','DEMO-001','000000000','demo@example.com','Opciones vegetarianas',1);
INSERT INTO catalog_items(id,version,name,kind,unit,price,stock,minimum,active) VALUES
(1,0,'Arroz','INSUMO','kg',5,80,10,1),(2,0,'Pollo','INSUMO','kg',12,5,10,1),
(3,0,'Arroz con pollo','PLATO','porción',20,0,0,1),(4,0,'Menú norteño','MENU','persona',35,0,0,1),
(5,0,'Personal adicional','EXTRA','hora',25,0,0,1);
INSERT INTO components(id,version,parent_id,ingredient_id,quantity) VALUES(1,0,3,1,0.12),(2,0,3,2,0.20),(3,0,4,3,1);
INSERT INTO quotations(id,version,client_id,code,created,valid_until,event_date,attendees,location,restrictions,status,discount,tax_percent,total)
VALUES(1,0,1,'COT-DEMO-001',CURRENT_DATE,DATE_ADD(CURRENT_DATE,INTERVAL 7 DAY),DATE_ADD(NOW(),INTERVAL 5 DAY),50,'Local de demostración Piura','5 porciones sin ají','APROBADA',0,0,1750);
INSERT INTO quote_lines(id,version,quotation_id,item_id,description,quantity,unit_price) VALUES(1,0,1,4,'Menú norteño',50,35);
INSERT INTO events(id,version,quotation_id,`date`,location,responsible,status,service,staff,equipment) VALUES(1,0,1,DATE_ADD(NOW(),INTERVAL 5 DAY),'Local de demostración Piura','coordinador','CONFIRMADO','Almuerzo corporativo','2 cocineros y 2 mozos','Mesas y menaje');
INSERT INTO payments(id,version,event_id,amount,`date`,method,reference,voided,actor) VALUES(1,0,1,500,CURRENT_DATE,'TRANSFERENCIA','DEMO',0,'DEMO');
