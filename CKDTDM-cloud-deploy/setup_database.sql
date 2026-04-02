-- ============================================================
-- SCRIPT TẠO DATABASE CHO HỆ THỐNG BÃI ĐỖ XE PHÂN TÁN
-- Chạy script này trên MySQL của TỪNG MÁY SERVER
-- ============================================================

CREATE DATABASE IF NOT EXISTS dtdm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dtdm;

-- Tạo cả 5 bảng (mỗi máy chỉ dùng bảng của mình, nhưng tạo đủ không sao)
CREATE TABLE IF NOT EXISTS server1 (
  vitri  VARCHAR(10) NOT NULL PRIMARY KEY,
  bienso VARCHAR(20),
  hieu   VARCHAR(20),
  mau    VARCHAR(20),
  gio    VARCHAR(50)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS server2 LIKE server1;
CREATE TABLE IF NOT EXISTS server3 LIKE server1;
CREATE TABLE IF NOT EXISTS server4 LIKE server1;
CREATE TABLE IF NOT EXISTS server5 LIKE server1;

-- Cấp quyền cho user root từ localhost
GRANT ALL PRIVILEGES ON dtdm.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

SELECT 'Database setup hoàn tất!' AS status;
