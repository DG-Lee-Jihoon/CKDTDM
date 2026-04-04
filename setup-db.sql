-- =====================================================
-- setup-db.sql — Tạo database cho 1 máy
-- Chạy trên TỪNG MÁY: mysql -u root -p < setup-db.sql
-- =====================================================

CREATE DATABASE IF NOT EXISTS ckdtdm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE parking_db;

CREATE TABLE IF NOT EXISTS vehicles (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    bien_so            VARCHAR(20)  NOT NULL UNIQUE,
    hang_xe            VARCHAR(50)  NOT NULL,
    khu                VARCHAR(10)  NOT NULL,
    lo                 VARCHAR(10)  NOT NULL,
    trang_thai         VARCHAR(20)  NOT NULL DEFAULT 'CHO_XE',
    ghi_chu            VARCHAR(255),
    updated_by_server  INT,
    created_at         DATETIME,
    updated_at         DATETIME
);

-- Dữ liệu mẫu (chỉ cần chạy trên 1 máy, sẽ đồng bộ tự động)
INSERT IGNORE INTO vehicles (bien_so, hang_xe, khu, lo, trang_thai, updated_by_server, created_at, updated_at) VALUES
('51A-12345', 'Toyota Camry',   'A', '01', 'CO_XE',    1, NOW(), NOW()),
('51B-67890', 'Honda Civic',    'A', '02', 'CO_XE',    1, NOW(), NOW()),
('30A-11111', 'Ford Ranger',    'B', '01', 'CHO_XE',   1, NOW(), NOW()),
('43C-55555', 'Mazda CX-5',     'B', '03', 'CO_XE',    1, NOW(), NOW()),
('51F-99999', 'Kia Morning',    'C', '01', 'BAO_TRI',  1, NOW(), NOW()),
('70A-22222', 'VinFast Lux',    'C', '02', 'DAT_TRUOC',1, NOW(), NOW());

SELECT 'Setup hoàn tất!' AS message;
