
-- ==========================================
-- QFLOW - SMART QUEUE MANAGEMENT SYSTEM
-- SQL QUERIES
-- ==========================================


-- 1. VIEW ALL CUSTOMERS

SELECT * FROM Customer;


-- 2. VIEW ALL SERVICES

SELECT * FROM Service;


-- 3. VIEW ALL COUNTERS

SELECT * FROM Counter;


-- 4. VIEW ALL TOKENS

SELECT * FROM Token;


-- 5. DISPLAY WAITING TOKENS BY PRIORITY

-- Emergency tokens first
-- Senior tokens second
-- Normal tokens last

SELECT
    token_number,
    priority,
    status,
    created_at
FROM Token
WHERE status = 'WAITING'
ORDER BY
    CASE priority
        WHEN 'EMERGENCY' THEN 1
        WHEN 'SENIOR' THEN 2
        WHEN 'NORMAL' THEN 3
    END,
    created_at ASC;


-- 6. DISPLAY TOKEN DETAILS WITH CUSTOMER AND SERVICE

SELECT
    t.token_number,
    c.name AS customer_name,
    c.phone,
    s.service_name,
    t.priority,
    t.status
FROM Token t
JOIN Customer c
    ON t.customer_id = c.customer_id
JOIN Service s
    ON t.service_id = s.service_id
ORDER BY t.token_number;


-- 7. DISPLAY TOKEN DETAILS WITH COUNTER

SELECT
    t.token_number,
    c.name AS customer_name,
    co.counter_name,
    s.service_name,
    t.priority,
    t.status
FROM Token t
JOIN Customer c
    ON t.customer_id = c.customer_id
LEFT JOIN Counter co
    ON t.counter_id = co.counter_id
JOIN Service s
    ON t.service_id = s.service_id
ORDER BY t.token_number;


-- 8. FIND A TOKEN USING TOKEN NUMBER

SELECT *
FROM Token
WHERE token_number = 103;


-- 9. VIEW COMPLETED SERVICE HISTORY

SELECT
    sh.history_id,
    t.token_number,
    c.name AS customer_name,
    sh.started_at,
    sh.completed_at,
    sh.service_duration,
    sh.remarks
FROM ServiceHistory sh
JOIN Token t
    ON sh.token_id = t.token_id
JOIN Customer c
    ON t.customer_id = c.customer_id;


-- 10. COUNT TOKENS BY STATUS

SELECT
    status,
    COUNT(*) AS total_tokens
FROM Token
GROUP BY status;


-- 11. COUNT TOKENS BY PRIORITY

SELECT
    priority,
    COUNT(*) AS total_tokens
FROM Token
GROUP BY priority;


-- 12. VIEW ACTIVE COUNTERS

SELECT *
FROM Counter
WHERE is_active = TRUE;


-- ==========================================
-- EXAMPLE UPDATE QUERIES
-- Run these separately when required.
-- Do not execute all updates together.
-- ==========================================

-- Assign a counter to a token

-- UPDATE Token
-- SET counter_id = 3
-- WHERE token_number = 103;


-- Change token status to SERVING

-- UPDATE Token
-- SET status = 'SERVING'
-- WHERE token_number = 103;


-- Change token status to COMPLETED

-- UPDATE Token
-- SET status = 'COMPLETED',
--     completed_at = CURRENT_TIMESTAMP
-- WHERE token_number = 103;


-- END OF QFLOW SQL QUERIES
