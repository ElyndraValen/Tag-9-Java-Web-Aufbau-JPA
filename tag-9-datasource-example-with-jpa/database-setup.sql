-- =================================================
-- Tag 9 - Datasource Beispiel - Datenbank Setup
-- =================================================

-- Datenbank erstellen (falls nicht vorhanden)
CREATE DATABASE IF NOT EXISTS mywebapp 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE mywebapp;

-- =================================================
-- Tabelle: users
-- =================================================
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- =================================================
-- Tabelle: blog_posts
-- =================================================
CREATE TABLE IF NOT EXISTS blog_posts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    author VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_author (author),
    INDEX idx_created_at (created_at),
    FULLTEXT INDEX idx_search (title, content)
) ENGINE=InnoDB;

-- =================================================
-- Test-Daten für users
-- =================================================
INSERT INTO users (username, email, password) VALUES
('elyndra', 'elyndra@java-developer.online', 'password123'),
('nova', 'nova@java-developer.online', 'password123'),
('cassian', 'cassian@java-developer.online', 'password123'),
('admin', 'admin@java-developer.online', 'admin123'),
('testuser', 'test@example.com', 'test123');

-- =================================================
-- Test-Daten für blog_posts
-- =================================================
INSERT INTO blog_posts (title, content, author) VALUES
('Warum Connection Pooling wichtig ist', 
 'Connection Pooling ist eine der wichtigsten Performance-Optimierungen in Java Web. In diesem Post erkläre ich, warum...', 
 'Elyndra Valen'),
 
('Meine Reise mit Java EE', 
 'Als Junior Developer habe ich am Anfang mit Connection Pooling gekämpft. Hier sind meine Learnings...', 
 'Nova Trent'),
 
('JDBC Best Practices', 
 'Nach 10 Jahren Java Enterprise Development kann ich sagen: Diese 5 JDBC-Patterns sollte jeder kennen...', 
 'Elyndra Valen'),
 
('PreparedStatement vs Statement', 
 'SQL Injection ist real! Deshalb ist PreparedStatement nicht optional, sondern Pflicht. Hier ist warum...', 
 'Elyndra Valen'),
 
('Datasource-Konfiguration in Payara', 
 'Step-by-step Guide: So konfigurierst du eine Datasource mit Connection Pool in Payara Server...', 
 'Nova Trent');

-- =================================================
-- Nützliche Queries für Testing
-- =================================================

-- Alle User anzeigen
-- SELECT * FROM users;

-- Alle Blog-Posts anzeigen
-- SELECT * FROM blog_posts ORDER BY created_at DESC;

-- Nach Blog-Posts suchen
-- SELECT * FROM blog_posts WHERE title LIKE '%Connection%' OR content LIKE '%Connection%';

-- User-Anzahl
-- SELECT COUNT(*) FROM users;

-- Posts pro Author
-- SELECT author, COUNT(*) as post_count FROM blog_posts GROUP BY author;

-- =================================================
-- Cleanup (falls nötig)
-- =================================================

-- DROP TABLE IF EXISTS blog_posts;
-- DROP TABLE IF EXISTS users;
-- DROP DATABASE IF EXISTS mywebapp;
