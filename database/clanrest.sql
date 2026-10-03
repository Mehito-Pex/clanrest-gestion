-- MySQL 8.0+ · ejecutar en una base vacía clanrest.
CREATE DATABASE IF NOT EXISTS clanrest CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clanrest;

CREATE TABLE clients (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `name` VARCHAR(120) NOT NULL,
  `document_number` VARCHAR(255),
  `phone` VARCHAR(255),
  `email` VARCHAR(255),
  `preferences` VARCHAR(1000),
  `active` BIT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE catalog_items (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `name` VARCHAR(255) NOT NULL,
  `kind` VARCHAR(255) NOT NULL,
  `unit` VARCHAR(255),
  `price` DECIMAL(12,2) NOT NULL,
  `stock` DECIMAL(12,3) NOT NULL,
  `minimum` DECIMAL(12,3) NOT NULL,
  `description` VARCHAR(1000),
  `active` BIT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE components (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `parent_id` BIGINT NOT NULL,
  `ingredient_id` BIGINT NOT NULL,
  `quantity` DECIMAL(12,3) NOT NULL,
  CONSTRAINT fk_components_parent_id FOREIGN KEY (`parent_id`) REFERENCES catalog_items(id) ON DELETE RESTRICT,
  CONSTRAINT fk_components_ingredient_id FOREIGN KEY (`ingredient_id`) REFERENCES catalog_items(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE quotations (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `client_id` BIGINT NOT NULL,
  `code` VARCHAR(255) UNIQUE,
  `created` DATE,
  `valid_until` DATE,
  `event_date` DATETIME(6),
  `attendees` INT NOT NULL,
  `location` VARCHAR(255),
  `restrictions` VARCHAR(1000),
  `status` VARCHAR(255),
  `discount` DECIMAL(12,2),
  `tax_percent` DECIMAL(5,2),
  `total` DECIMAL(12,2),
  CONSTRAINT fk_quotations_client_id FOREIGN KEY (`client_id`) REFERENCES clients(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE quote_lines (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `quotation_id` BIGINT NOT NULL,
  `item_id` BIGINT NOT NULL,
  `description` VARCHAR(255),
  `quantity` INT NOT NULL,
  `unit_price` DECIMAL(12,2),
  CONSTRAINT fk_quote_lines_quotation_id FOREIGN KEY (`quotation_id`) REFERENCES quotations(id) ON DELETE RESTRICT,
  CONSTRAINT fk_quote_lines_item_id FOREIGN KEY (`item_id`) REFERENCES catalog_items(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE events (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `quotation_id` BIGINT NOT NULL UNIQUE,
  `date` DATETIME(6),
  `location` VARCHAR(255),
  `responsible` VARCHAR(255),
  `status` VARCHAR(255),
  `service` VARCHAR(1000),
  `staff` VARCHAR(1000),
  `equipment` VARCHAR(1000),
  `notes` VARCHAR(2000),
  CONSTRAINT fk_events_quotation_id FOREIGN KEY (`quotation_id`) REFERENCES quotations(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE event_tasks (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `event_id` BIGINT NOT NULL,
  `description` VARCHAR(255),
  `responsible` VARCHAR(255),
  `due_date` DATE,
  `done` BIT NOT NULL,
  CONSTRAINT fk_event_tasks_event_id FOREIGN KEY (`event_id`) REFERENCES events(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE stock_movements (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `item_id` BIGINT NOT NULL,
  `event_id` BIGINT,
  `kind` VARCHAR(255),
  `quantity` DECIMAL(12,3),
  `resulting_stock` DECIMAL(12,3),
  `reason` VARCHAR(255),
  `actor` VARCHAR(255),
  `timestamp` DATETIME(6),
  CONSTRAINT fk_stock_movements_item_id FOREIGN KEY (`item_id`) REFERENCES catalog_items(id) ON DELETE RESTRICT,
  CONSTRAINT fk_stock_movements_event_id FOREIGN KEY (`event_id`) REFERENCES events(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE payments (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `event_id` BIGINT NOT NULL,
  `amount` DECIMAL(12,2),
  `date` DATE,
  `method` VARCHAR(255),
  `reference` VARCHAR(255),
  `voided` BIT NOT NULL,
  `void_reason` VARCHAR(255),
  `actor` VARCHAR(255),
  CONSTRAINT fk_payments_event_id FOREIGN KEY (`event_id`) REFERENCES events(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE app_users (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `full_name` VARCHAR(255),
  `role` VARCHAR(255) NOT NULL,
  `active` BIT NOT NULL
) ENGINE=InnoDB;

CREATE TABLE audit_entries (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  version BIGINT,
  `timestamp` DATETIME(6),
  `actor` VARCHAR(255),
  `action` VARCHAR(255),
  `entity_name` VARCHAR(255),
  `entity_id` BIGINT,
  `detail` VARCHAR(2000)
) ENGINE=InnoDB;

CREATE INDEX idx_events_date ON events (`date`);
CREATE INDEX idx_quotes_status ON quotations(status);
CREATE INDEX idx_audit_timestamp ON audit_entries(`timestamp`);
CREATE INDEX idx_stock_timestamp ON stock_movements(`timestamp`);
