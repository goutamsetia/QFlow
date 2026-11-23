
-- QFLOW - SMART QUEUE MANAGEMENT SYSTEM
-- DATABASE SCHEMA

--1. CUSTOMER TABLE

CREATE TABLE IF NOT EXISTS Customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    email VARCHAR(100) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- 2. SERVICE TABLE

CREATE TABLE IF NOT EXISTS Service (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    average_time INT NOT NULL DEFAULT 10,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;


-- 3. COUNTER TABLE

CREATE TABLE IF NOT EXISTS Counter (
    counter_id INT AUTO_INCREMENT PRIMARY KEY,
    counter_name VARCHAR(50) NOT NULL,
    service_id INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    FOREIGN KEY (service_id)
        REFERENCES Service(service_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;


-- 4. TOKEN TABLE

CREATE TABLE IF NOT EXISTS Token (
    token_id INT AUTO_INCREMENT PRIMARY KEY,
    token_number INT NOT NULL,
    issue_date DATE NOT NULL DEFAULT (CURRENT_DATE),

    customer_id INT NOT NULL,
    service_id INT NOT NULL,
    counter_id INT DEFAULT NULL,

    priority ENUM('NORMAL', 'SENIOR', 'EMERGENCY')
        NOT NULL DEFAULT 'NORMAL',

    status ENUM('WAITING', 'SERVING', 'COMPLETED', 'CANCELLED')
        NOT NULL DEFAULT 'WAITING',

    estimated_wait_time INT NOT NULL DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL DEFAULT NULL,

    UNIQUE (token_number, issue_date),

    FOREIGN KEY (customer_id)
        REFERENCES Customer(customer_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    FOREIGN KEY (service_id)
        REFERENCES Service(service_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    FOREIGN KEY (counter_id)
        REFERENCES Counter(counter_id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) ENGINE=InnoDB;


-- 5. SERVICE HISTORY TABLE

CREATE TABLE IF NOT EXISTS ServiceHistory (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    token_id INT NOT NULL UNIQUE,

    started_at TIMESTAMP NULL DEFAULT NULL,
    completed_at TIMESTAMP NOT NULL,

    service_duration INT,
    remarks VARCHAR(255),

    FOREIGN KEY (token_id)
        REFERENCES Token(token_id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE=InnoDB;


-- 6. ADMIN TABLE

CREATE TABLE IF NOT EXISTS Admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,

    role ENUM('ADMIN', 'STAFF')
        NOT NULL DEFAULT 'STAFF',

    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
