# 🗄️ Database Scripts & Migration — Cover Letter Creator

Thư mục này chứa toàn bộ các tập lệnh SQL phục vụ cho việc khởi tạo, nạp dữ liệu mẫu và cập nhật cấu trúc cơ sở dữ liệu trên **TiDB Cloud Serverless** hoặc **MySQL / XAMPP Local**.

---

## 📁 Danh Mục Các Tập Lệnh

| Tên file | Mục đích sử dụng | Khi nào cần chạy? |
| :--- | :--- | :--- |
| **`seed_templates.sql`** | **Nạp dữ liệu mẫu (11 Templates)**: 6 mẫu Cover Letter (3 Nhà Nước + 3 Doanh Nghiệp) và 5 mẫu Modern CV (2 Nhà Nước + 3 Hiện Đại) kèm toàn bộ HTML/CSS. | **Bắt buộc** khi khởi tạo database mới để có sẵn các mẫu hồ sơ hiển thị trên web. |
| **`update_tidb_database.sql`** | **Cập nhật Migration**: Tạo mới bảng `followed_cvs`, `vip_upgrade_requests`, `password_reset_token` và bổ sung các cột `avatar_url`, `status`, `source`, `file_size`. Dùng cú pháp `IF NOT EXISTS` an toàn. | Dùng khi muốn nâng cấp database hiện tại mà **không làm mất dữ liệu tài khoản hay hồ sơ cũ**. |
| **`init_tidb_database.sql`** | **Khởi tạo trọn gói từ đầu**: Tạo đầy đủ bảng biểu của hệ thống. | Dùng khi dựng mới hoàn toàn database từ con số 0 trên một cụm máy chủ mới. |

---

## 🚀 Hướng Dẫn Sử Dụng

### 1. Trên TiDB Cloud Console (Web SQL Editor)
1. Đăng nhập [TiDB Cloud](https://tidbcloud.com/) -> Chọn Cluster -> Vào tab **SQL Editor**.
2. **Nếu nâng cấp database đang chạy**: Mở file `update_tidb_database.sql`, copy toàn bộ nội dung và dán vào SQL Editor -> Bấm **Run**.
3. **Nếu database vừa tạo mới chưa có dữ liệu**:
   - Chạy `init_tidb_database.sql`
   - Chạy tiếp `seed_templates.sql`
   - Chạy `update_tidb_database.sql`

### 2. Trên Máy Cục Bộ (MySQL CLI / phpMyAdmin / XAMPP)
```bash
# Nạp dữ liệu mẫu vào database:
mysql -u root -p cover_letter_creator_db < database/seed_templates.sql

# Chạy cập nhật migration:
mysql -u root -p cover_letter_creator_db < database/update_tidb_database.sql
```
