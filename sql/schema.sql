-- PennyWise database schema (MySQL 8). Run once:  mysql -u root -p < sql/schema.sql
CREATE DATABASE IF NOT EXISTS pennywise CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pennywise;

CREATE TABLE IF NOT EXISTS users (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  name           VARCHAR(80)  NOT NULL,
  email          VARCHAR(120) NOT NULL UNIQUE,
  role           ENUM('ADMIN','ADVISOR','USER') NOT NULL,
  password_hash  VARCHAR(200) NOT NULL,          -- salt$PBKDF2 hash, never plain text
  monthly_income DECIMAL(12,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS expenses (
  id           INT AUTO_INCREMENT PRIMARY KEY,
  user_id      INT NOT NULL,
  category     VARCHAR(40)   NOT NULL,
  amount       DECIMAL(12,2) NOT NULL,
  expense_date DATE          NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS budgets (
  id       INT AUTO_INCREMENT PRIMARY KEY,
  user_id  INT NOT NULL,
  category VARCHAR(40)   NOT NULL,
  amount   DECIMAL(12,2) NOT NULL,
  UNIQUE KEY uq_user_category (user_id, category),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS advice (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT NOT NULL,
  advisor_id INT NULL,
  content    TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id)    REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (advisor_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS messages (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  user_id     INT NOT NULL,                       -- the client the conversation is about
  sender_role ENUM('USER','ADVISOR') NOT NULL,
  content     TEXT NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS feedback (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT NOT NULL,
  content    TEXT NOT NULL,
  status     ENUM('OPEN','RESOLVED') NOT NULL DEFAULT 'OPEN',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS settings (
  id     INT AUTO_INCREMENT PRIMARY KEY,
  skey   VARCHAR(40)  NOT NULL UNIQUE,
  svalue VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS security_log (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  event_text VARCHAR(200) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Demo users and sample data are inserted automatically on first start
-- (see com.pennywise.service.DemoData), so passwords are hashed correctly.
