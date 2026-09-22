-- =====================================================================
-- SCRIPT CẬP NHẬT DATABASE TI-DB CLOUD CHO COVER LETTER CREATOR
-- Sử dụng khi: Bạn muốn cập nhật DB hiện tại mà không làm mất dữ liệu cũ.
-- =====================================================================

USE cover_letter_creator_db;

-- 1. Bổ sung cột avatar_url cho bảng users (nếu chưa có)
ALTER TABLE `users` ADD COLUMN IF NOT EXISTS `avatar_url` VARCHAR(1000) DEFAULT NULL;

-- 2. Bổ sung cột status cho các bảng templates (nếu chưa có)
ALTER TABLE `templates` ADD COLUMN IF NOT EXISTS `status` VARCHAR(50) DEFAULT 'active';
ALTER TABLE `modern_cv_templates` ADD COLUMN IF NOT EXISTS `status` VARCHAR(50) DEFAULT 'active';

-- 3. Bổ sung cột source và file_size cho bảng followed_cvs (nếu bảng đã tồn tại)
-- (Nếu bảng chưa có, lệnh CREATE TABLE bên dưới sẽ tự động tạo đủ)
CREATE TABLE IF NOT EXISTS `followed_cvs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `url_google_drive` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `note` VARCHAR(255) DEFAULT NULL,
  `company` VARCHAR(255) DEFAULT NULL,
  `status` VARCHAR(255) NOT NULL DEFAULT 'pending',
  `source` VARCHAR(255) DEFAULT 'system',
  `file_size` BIGINT DEFAULT NULL,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  KEY `FK_followed_cvs_user` (`user_id`),
  CONSTRAINT `FK_followed_cvs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Thêm cột nếu bảng followed_cvs đã tồn tại từ phiên bản cũ
ALTER TABLE `followed_cvs` ADD COLUMN IF NOT EXISTS `source` VARCHAR(255) DEFAULT 'system';
ALTER TABLE `followed_cvs` ADD COLUMN IF NOT EXISTS `file_size` BIGINT DEFAULT NULL;

-- 4. Tạo bảng yêu cầu nâng cấp VIP (vip_upgrade_requests)
CREATE TABLE IF NOT EXISTS `vip_upgrade_requests` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `user_email` VARCHAR(255) NOT NULL,
  `plan` VARCHAR(255) NOT NULL,
  `status` VARCHAR(255) NOT NULL DEFAULT 'pending',
  `note` VARCHAR(500) DEFAULT NULL,
  `admin_note` VARCHAR(500) DEFAULT NULL,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_vip_upgrade_user` (`user_id`),
  CONSTRAINT `FK_vip_upgrade_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 5. Tạo bảng token quên mật khẩu (password_reset_token - nếu chưa có)
CREATE TABLE IF NOT EXISTS `password_reset_token` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `expiry_date` DATETIME(6) DEFAULT NULL,
  `token` VARCHAR(255) DEFAULT NULL,
  `used` BIT(1) NOT NULL DEFAULT b'0',
  `user_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_pwd_reset_user` (`user_id`),
  CONSTRAINT `FK_pwd_reset_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
