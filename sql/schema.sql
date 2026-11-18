-- =====================================================================
-- QFlow Database Schema (MySQL 8)
-- Version: 1.1.0 (Phase 2 Frozen Design)
-- Description: Complete 3NF Normalized Schema for QFlow Smart Queue System
-- =====================================================================

DROP DATABASE IF EXISTS qflow_db;
CREATE DATABASE qflow_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qflow_db;

-- ---------------------------------------------------------------------
-- 1. CUSTOMERS TABLE
-- Stores permanent customer demographic profiles.
-- Deduplicated by unique phone number and unique email address.
-- ---------------------------------------------------------------------
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_code VARCHAR(32) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    age INT NOT NULL,
    category ENUM('STUDENT', 'STAFF', 'SENIOR_CITIZEN', 'GUEST') NOT NULL DEFAULT 'STUDENT',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_customer_age CHECK (age >= 16 AND age <= 120),
    CONSTRAINT chk_customer_name CHECK (CHAR_LENGTH(TRIM(full_name)) >= 2)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 2. SERVICES TABLE
-- Defines the 4 logical service departments in the college administration.
-- ---------------------------------------------------------------------
CREATE TABLE services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_code VARCHAR(10) NOT NULL UNIQUE, -- 'REG', 'ACC', 'ADM', 'EXM'
    service_name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    avg_duration_minutes INT NOT NULL DEFAULT 10,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_avg_duration CHECK (avg_duration_minutes > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. COUNTERS TABLE
-- Physical/logical service desks staffed by counter operators.
-- ---------------------------------------------------------------------
CREATE TABLE counters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    counter_number INT NOT NULL UNIQUE,
    service_id BIGINT NOT NULL,
    operator_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_counters_service FOREIGN KEY (service_id) 
        REFERENCES services(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. DAILY SERVICE SEQUENCES TABLE
-- Allocates atomic, crash-safe, daily-resetting sequence numbers.
-- Composite Primary Key: (service_id, sequence_date).
-- ---------------------------------------------------------------------
CREATE TABLE daily_service_sequences (
    service_id BIGINT NOT NULL,
    sequence_date DATE NOT NULL,
    current_sequence INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (service_id, sequence_date),
    CONSTRAINT fk_sequences_service FOREIGN KEY (service_id) 
        REFERENCES services(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. TOKENS TABLE
-- Core operational entity tracking status, priority, and timestamps.
-- Dual-indexed for high-performance queue polling and crash recovery.
-- ---------------------------------------------------------------------
CREATE TABLE tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_number VARCHAR(35) NOT NULL UNIQUE, -- e.g. 'TKN-ACC-20261118-0001'
    customer_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    counter_id BIGINT NULL,
    priority ENUM('EMERGENCY', 'SENIOR_CITIZEN', 'NORMAL') NOT NULL,
    status ENUM('WAITING', 'SERVING', 'COMPLETED', 'SKIPPED', 'CANCELLED', 'INTERRUPTED') NOT NULL DEFAULT 'WAITING',
    arrival_sequence BIGINT NOT NULL,
    turns_skipped INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    served_at DATETIME NULL,
    completed_at DATETIME NULL,
    CONSTRAINT fk_tokens_customer FOREIGN KEY (customer_id) 
        REFERENCES customers(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_tokens_service FOREIGN KEY (service_id) 
        REFERENCES services(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_tokens_counter FOREIGN KEY (counter_id) 
        REFERENCES counters(id) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB;

-- Composite indexes for query performance & startup reconciliation
CREATE INDEX idx_tokens_queue_recovery ON tokens(service_id, status, priority, arrival_sequence);
CREATE INDEX idx_tokens_customer ON tokens(customer_id, created_at);
CREATE INDEX idx_tokens_status_created ON tokens(status, created_at);

-- ---------------------------------------------------------------------
-- 6. SERVICE HISTORY TABLE
-- Immutable audit log for completed or skipped service sessions.
-- Powers Java Stream API analytics and operational reporting.
-- ---------------------------------------------------------------------
CREATE TABLE service_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token_id BIGINT NOT NULL,
    counter_id BIGINT NOT NULL,
    operator_name VARCHAR(100) NOT NULL,
    final_status ENUM('COMPLETED', 'SKIPPED', 'CANCELLED', 'MANUALLY_RESOLVED') NOT NULL,
    service_start_time DATETIME NOT NULL,
    service_end_time DATETIME NOT NULL,
    duration_seconds INT NOT NULL,
    operator_remarks VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_token FOREIGN KEY (token_id) 
        REFERENCES tokens(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_history_counter FOREIGN KEY (counter_id) 
        REFERENCES counters(id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE INDEX idx_history_dates ON service_history(service_start_time, final_status);

-- =====================================================================
-- INITIAL SEED DATA FOR SIMULATION ENVIRONMENT
-- =====================================================================
INSERT INTO services (service_code, service_name, description, avg_duration_minutes) VALUES
('REG', 'Registrar & Transcripts', 'Official academic transcripts, degrees, and certificates', 12),
('ACC', 'Accounts & Fee Payments', 'Tuition payments, scholarship disbursement, and fines', 8),
('ADM', 'Admissions & Verification', 'Enrollment verification, eligibility certificates, and ID issuance', 15),
('EXM', 'Examinations & Grade Cards', 'Hall tickets, re-evaluations, and mark sheets', 10);

INSERT INTO counters (counter_number, service_id, operator_name, is_active) VALUES
(1, 1, 'Prof. Sharma (Registrar)', TRUE),
(2, 2, 'Mr. Verma (Accounts)', TRUE),
(3, 3, 'Ms. Kulkarni (Admissions)', TRUE),
(4, 4, 'Dr. Nair (Examinations)', TRUE);
