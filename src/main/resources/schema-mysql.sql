-- ===================================================================
-- E-Commerce Application Database Initialization Script
-- Target Engine: MySQL 8.x / MariaDB
-- ===================================================================

-- 1. Create the Database Schema
-- Architectural Note (Interview Prep):
-- 'utf8mb4' is chosen over standard 'utf8' because MySQL's legacy 'utf8' only supports up to 3 bytes per character.
-- 'utf8mb4' supports full 4-byte Unicode (including emojis, symbols, and multilingual product descriptions).
-- 'utf8mb4_unicode_ci' provides accurate sorting and case-insensitive comparison across international languages.
CREATE DATABASE IF NOT EXISTS ecommerce_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ecommerce_db;

-- 2. Optional: Dedicated Non-Root Application User (Principle of Least Privilege)
-- In production, the application should never connect as the database 'root' superuser.
-- UNCOMMENT below to create a dedicated user:
-- CREATE USER IF NOT EXISTS 'ecommerce_user'@'localhost' IDENTIFIED BY 'ecommerce_password';
-- GRANT ALL PRIVILEGES ON ecommerce_db.* TO 'ecommerce_user'@'localhost';
-- FLUSH PRIVILEGES;

-- 3. Verification Query
SELECT 'Database ecommerce_db successfully initialized!' AS status;
