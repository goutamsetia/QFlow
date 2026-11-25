
-- ==========================================
-- QFLOW - SMART QUEUE MANAGEMENT SYSTEM
-- SAMPLE DATA
-- ==========================================

-- 1. INSERT SAMPLE SERVICES

INSERT INTO Service
(service_id, service_name, description, average_time, is_active)
VALUES
(1, 'General Enquiry', 'General customer queries', 10, TRUE),
(2, 'Billing', 'Payment and billing services', 15, TRUE),
(3, 'Documentation', 'Document verification and processing', 20, TRUE),
(4, 'Complaint', 'Customer complaint handling', 12, TRUE);


-- 2. INSERT SAMPLE CUSTOMERS

INSERT INTO Customer
(customer_id, name, phone, email)
VALUES
(1, 'Rahul Sharma', '9876500001', 'rahul@example.com'),
(2, 'Aman Verma', '9876500002', 'aman@example.com'),
(3, 'Priya Singh', '9876500003', 'priya@example.com'),
(4, 'Karan Kumar', '9876500004', 'karan@example.com');


-- 3. INSERT SAMPLE COUNTERS

INSERT INTO Counter
(counter_id, counter_name, service_id, is_active)
VALUES
(1, 'Counter 1', 1, TRUE),
(2, 'Counter 2', 2, TRUE),
(3, 'Counter 3', 3, TRUE),
(4, 'Counter 4', 4, TRUE);


-- 4. INSERT SAMPLE TOKENS

INSERT INTO Token
(token_number, customer_id, service_id, counter_id,
 priority, status, estimated_wait_time)
VALUES
(101, 1, 1, NULL, 'NORMAL', 'WAITING', 10),
(102, 2, 2, NULL, 'SENIOR', 'WAITING', 15),
(103, 3, 3, NULL, 'EMERGENCY', 'WAITING', 20),
(104, 4, 4, NULL, 'NORMAL', 'WAITING', 12);


-- END OF SAMPLE DATA
