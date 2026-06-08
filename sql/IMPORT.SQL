-- =============================================================
-- IMPORT.sql — импорт пользователей из user_import.xlsx
-- =============================================================

-- -------------------------------------------------------------
-- 1. Таблица для сырого импорта
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS users_import (
    role_name VARCHAR(100),
    full_name VARCHAR(255),
    login     VARCHAR(255),
    password  VARCHAR(255)
);


-- -------------------------------------------------------------
-- 2. Справочник ролей из таблицы импорта
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS roles (
    id   SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO roles (name)
SELECT DISTINCT role_name
FROM users_import
ON CONFLICT (name) DO NOTHING;


-- -------------------------------------------------------------
-- 3. Основная таблица пользователей через JOIN
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS users (
    id        SERIAL PRIMARY KEY,
    role_id   INTEGER      NOT NULL REFERENCES roles(id),
    full_name VARCHAR(255) NOT NULL,
    login     VARCHAR(255) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL
);

INSERT INTO users (role_id, full_name, login, password)
SELECT r.id, i.full_name, i.login, i.password
FROM   users_import i
JOIN   roles r ON r.name = i.role_name
ON CONFLICT (login) DO NOTHING;