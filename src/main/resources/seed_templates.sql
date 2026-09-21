-- ==============================================================================
-- SEED DATA: COVER LETTER TEMPLATES & MODERN CV TEMPLATES
-- ==============================================================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 1. BẢNG TEMPLATES (COVER LETTER - ĐƠN XIN VIỆC)
DELETE FROM templates;

INSERT INTO templates (name, type, content, image, views, status, update_date) VALUES
(
    'Đơn Xin Việc - Kỹ Sư Phần Mềm (Software Engineer)',
    'Công nghệ thông tin',
    '<div style=\"font-family: Arial, sans-serif; font-size: 13pt; line-height: 1.6; color: #333; max-width: 800px; margin: 0 auto; padding: 20px;\">
        <div style=\"text-align: center; margin-bottom: 25px;\">
            <h4 style=\"margin: 0; text-transform: uppercase;\">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</h4>
            <p style=\"margin: 5px 0 0 0; font-weight: bold;\">Độc lập – Tự do – Hạnh phúc</p>
            <div style=\"width: 160px; height: 1.5px; background: #333; margin: 8px auto;\"></div>
        </div>

        <div style=\"text-align: center; margin-bottom: 30px;\">
            <h2 style=\"margin: 0; text-transform: uppercase; color: #1976d2;\">ĐƠN XIN VIỆC</h2>
            <p style=\"font-style: italic; margin-top: 5px;\">(Vị trí: Kỹ sư Lập trình Fullstack / Backend Developer)</p>
        </div>

        <p><strong>Kính gửi:</strong> Ban Giám đốc cùng Bộ phận Tuyển dụng Công ty <em>[Tên Doanh Nghiệp]</em>,</p>

        <p>Tôi tên là: <strong>[Họ và Tên của Bạn]</strong><br>
        Sinh ngày: [DD/MM/YYYY]<br>
        Địa chỉ: [Quận/Huyện, Tỉnh/Thành phố]<br>
        Số điện thoại: [Số điện thoại liên hệ] &nbsp;|&nbsp; Email: [Địa chỉ Email]</p>

        <p>Qua tìm hiểu thông tin tuyển dụng trên [Cổng tuyển dụng/Website Công ty], tôi được biết Quý công ty đang tìm kiếm ứng viên cho vị trí <strong>Kỹ sư Lập trình Phần mềm</strong>. Với hơn 2 năm kinh nghiệm thực chiến trong việc phát triển hệ thống web ứng dụng Java Spring Boot kết hợp React, cùng niềm đam mê tối ưu hóa hiệu năng và trải nghiệm người dùng, tôi tin rằng năng lực của mình hoàn toàn đáp ứng được mục tiêu phát triển sản phẩm của Quý công ty.</p>

        <p>Trong quá trình làm việc trước đây, tôi đã trực tiếp tham gia:</p>
        <ul>
            <li>Thiết kế và triển khai kiến trúc RESTful API chuẩn mực, xử lý hàng nghìn giao dịch mỗi ngày.</li>
            <li>Tối ưu hóa truy vấn cơ sở dữ liệu MySQL và PostgreSQL, giảm độ trễ phản hồi xuống dưới 150ms.</li>
            <li>Tích hợp các dịch vụ đám mây, lưu trữ Cloudflare R2 / AWS S3 và tự động hóa quy trình CI/CD qua Docker.</li>
        </ul>

        <p>Tôi đánh giá rất cao môi trường làm việc đổi mới sáng tạo và định hướng công nghệ tiên tiến tại Quý công ty. Tôi rất mong có cơ hội được tham dự một buổi phỏng vấn trực tiếp để trình bày rõ hơn về kinh nghiệm, đồ án đã triển khai và thảo luận về những đóng góp cụ thể mà tôi có thể mang lại.</p>

        <p>Xin chân thành cảm ơn Quý công ty đã dành thời gian quý báu xem xét hồ sơ của tôi.</p>

        <div style=\"margin-top: 40px; display: flex; justify-content: space-between;\">
            <div></div>
            <div style=\"text-align: center;\">
                <p style=\"margin: 0; font-style: italic;\">[Địa danh], ngày [DD] tháng [MM] năm [YYYY]</p>
                <p style=\"margin: 10px 0 60px 0; font-weight: bold;\">Người làm đơn</p>
                <p style=\"margin: 0; font-weight: bold;\">[Họ và Tên của Bạn]</p>
            </div>
        </div>
    </div>',
    'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&q=80',
    128,
    'active',
    NOW()
),
(
    'Đơn Xin Việc - Chuyên Viên Marketing & Tăng Trưởng',
    'Marketing / Truyền thông',
    '<div style=\"font-family: Arial, sans-serif; font-size: 13pt; line-height: 1.6; color: #333; max-width: 800px; margin: 0 auto; padding: 20px;\">
        <div style=\"text-align: center; margin-bottom: 25px;\">
            <h4 style=\"margin: 0; text-transform: uppercase;\">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</h4>
            <p style=\"margin: 5px 0 0 0; font-weight: bold;\">Độc lập – Tự do – Hạnh phúc</p>
            <div style=\"width: 160px; height: 1.5px; background: #333; margin: 8px auto;\"></div>
        </div>

        <div style=\"text-align: center; margin-bottom: 30px;\">
            <h2 style=\"margin: 0; text-transform: uppercase; color: #e91e63;\">ĐƠN ỨNG TUYỂN</h2>
            <p style=\"font-style: italic; margin-top: 5px;\">(Vị trí: Chuyên viên Digital Marketing & Growth Lead)</p>
        </div>

        <p><strong>Kính gửi:</strong> Phòng Nhân sự & Trưởng bộ phận Marketing Công ty <em>[Tên Công Ty]</em>,</p>

        <p>Tôi tên là: <strong>[Họ và Tên của Bạn]</strong><br>
        Số điện thoại: [Số điện thoại] &nbsp;|&nbsp; Email: [Email của Bạn]</p>

        <p>Tôi viết đơn này để bày tỏ nguyện vọng ứng tuyển vào vị trí <strong>Chuyên viên Digital Marketing</strong> tại Quý công ty theo thông báo tuyển dụng ngày [DD/MM/YYYY].</p>

        <p>Tốt nghiệp chuyên ngành Marketing tại [Tên trường Đại học] với hơn 3 năm kinh nghiệm lập kế hoạch và triển khai chiến dịch đa kênh (SEO, Google Ads, Facebook & TikTok Ads), tôi đã giúp các doanh nghiệp trước đây tăng trưởng hơn 45% lưu lượng truy cập tự nhiên và giảm 25% chi phí sở hữu khách hàng (CAC).</p>

        <p>Với khả năng phân tích số liệu nhạy bén, tư duy nội dung sáng tạo và sự am hiểu sâu sắc về hành vi người dùng số, tôi tin tưởng sẽ đóng góp tích cực vào các mục tiêu tăng trưởng doanh thu của Quý công ty.</p>

        <p>Rất mong có cơ hội trao đổi trực tiếp cùng Quý công ty.</p>

        <div style=\"margin-top: 40px; text-align: right;\">
            <p style=\"margin: 0; font-style: italic;\">Ngày [DD] tháng [MM] năm [YYYY]</p>
            <p style=\"margin: 10px 0 50px 0; font-weight: bold;\">Người nộp đơn</p>
            <p style=\"margin: 0; font-weight: bold;\">[Họ và Tên của Bạn]</p>
        </div>
    </div>',
    'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=400&q=80',
    95,
    'active',
    NOW()
),
(
    'Đơn Xin Việc - Quản Lý Dự Án / Project Manager',
    'Quản trị / Quản lý dự án',
    '<div style=\"font-family: Arial, sans-serif; font-size: 13pt; line-height: 1.6; color: #333; max-width: 800px; margin: 0 auto; padding: 20px;\">
        <div style=\"text-align: center; margin-bottom: 25px;\">
            <h4 style=\"margin: 0; text-transform: uppercase;\">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</h4>
            <p style=\"margin: 5px 0 0 0; font-weight: bold;\">Độc lập – Tự do – Hạnh phúc</p>
            <div style=\"width: 160px; height: 1.5px; background: #333; margin: 8px auto;\"></div>
        </div>

        <div style=\"text-align: center; margin-bottom: 30px;\">
            <h2 style=\"margin: 0; text-transform: uppercase; color: #2e7d32;\">THƯ ỨNG TUYỂN VỊ TRÍ QUẢN LÝ DỰ ÁN</h2>
        </div>

        <p><strong>Kính gửi:</strong> Ban Giám Đốc Công ty <em>[Tên Công Ty]</em>,</p>

        <p>Tôi tên là <strong>[Họ và Tên]</strong>. Tôi viết thư này để bày tỏ mong muốn được đồng hành cùng Quý công ty với vai trò <strong>IT Project Manager / Scrum Master</strong>.</p>

        <p>Với chứng chỉ PMP và hơn 4 năm kinh nghiệm dẫn dắt các đội ngũ kỹ thuật phát triển sản phẩm Agile/Scrum, tôi đã hoàn thành hơn 12 dự án đúng thời hạn và ngân sách cam kết. Điểm mạnh lớn nhất của tôi là khả năng kết nối giữa nhu cầu kinh doanh của khách hàng và năng lực công nghệ của đội ngũ phát triển, đồng thời xây dựng văn hóa làm việc minh bạch, chủ động.</p>

        <p>Tôi rất ấn tượng với những bước tiến đột phá của Quý công ty trên thị trường hiện nay và rất mong muốn được cống hiến năng lực quản trị của mình.</p>

        <p>Trân trọng cảm ơn Quý công ty.</p>

        <div style=\"margin-top: 40px; text-align: right;\">
            <p style=\"margin: 0; font-weight: bold;\">[Họ và Tên của Bạn]</p>
        </div>
    </div>',
    'https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=400&q=80',
    76,
    'active',
    NOW()
);

-- 2. BẢNG MODERN_CV_TEMPLATES (CV HIỆN ĐẠI ĐA SECTION)
DELETE FROM modern_cv_templates;

INSERT INTO modern_cv_templates (name, type, content, image, views, status, update_date) VALUES
(
    'Modern Tech Pro - Chuyên Nghiệp Tối Giản',
    'Công nghệ thông tin',
    '<div style=\"font-family: Roboto, Arial, sans-serif; color: #2c3e50; line-height: 1.5; padding: 25px; background: #ffffff; max-width: 850px; margin: auto; border: 1px solid #e0e0e0; border-radius: 8px;\">
        <!-- Header -->
        <div style=\"border-bottom: 2px solid #1976d2; padding-bottom: 15px; margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center;\">
            <div>
                <h1 style=\"margin: 0; font-size: 26pt; color: #1976d2; text-transform: uppercase; letter-spacing: 1px;\">NGUYỄN VĂN AN</h1>
                <h3 style=\"margin: 5px 0 0 0; font-size: 14pt; color: #546e7a; font-weight: 500;\">SENIOR FULLSTACK DEVELOPER</h3>
            </div>
            <div style=\"text-align: right; font-size: 10.5pt; color: #455a64;\">
                <p style=\"margin: 3px 0;\">📧 contact@nguyenvanan.dev</p>
                <p style=\"margin: 3px 0;\">📱 (+84) 987 654 321</p>
                <p style=\"margin: 3px 0;\">📍 Cầu Giấy, Hà Nội</p>
                <p style=\"margin: 3px 0;\">🌐 github.com/nguyenvanan</p>
            </div>
        </div>

        <!-- Layout 2 Cột -->
        <div style=\"display: grid; grid-template-columns: 2fr 1fr; gap: 25px;\">
            <!-- Cột Trái -->
            <div>
                <!-- Tóm tắt -->
                <div style=\"margin-bottom: 20px;\">
                    <h3 style=\"margin: 0 0 8px 0; font-size: 13pt; color: #1976d2; border-bottom: 1px solid #b0bec5; padding-bottom: 4px; text-transform: uppercase;\">MỤC TIÊU NGHỀ NGHIỆP</h3>
                    <p style=\"margin: 0; font-size: 11pt; color: #37474f;\">Kỹ sư phần mềm với hơn 4 năm kinh nghiệm thiết kế kiến trúc hệ thống phân tán, xây dựng Microservices hiệu năng cao với Spring Boot và xây dựng giao diện React mượt mà. Mong muốn đóng góp năng lực kỹ thuật và kỹ năng giải quyết vấn đề phức tạp vào các sản phẩm quy mô hàng triệu người dùng.</p>
                </div>

                <!-- Kinh nghiệm làm việc -->
                <div style=\"margin-bottom: 20px;\">
                    <h3 style=\"margin: 0 0 12px 0; font-size: 13pt; color: #1976d2; border-bottom: 1px solid #b0bec5; padding-bottom: 4px; text-transform: uppercase;\">KINH NGHIỆM LÀM VIỆC</h3>
                    
                    <div style=\"margin-bottom: 15px;\">
                        <div style=\"display: flex; justify-content: space-between;\">
                            <strong style=\"font-size: 11.5pt;\">Tech Lead / Fullstack Engineer</strong>
                            <span style=\"color: #78909c; font-size: 10pt;\">03/2023 – Hiện tại</span>
                        </div>
                        <div style=\"color: #1e88e5; font-style: italic; font-size: 10.5pt; margin-bottom: 5px;\">Công ty Cổ phần Công nghệ ABC</div>
                        <ul style=\"margin: 0; padding-left: 20px; font-size: 10.5pt; color: #37474f;\">
                            <li>Chịu trách nhiệm kiến trúc dịch vụ Core Banking, tối ưu hóa thông lượng API tăng 40%.</li>
                            <li>Tích hợp AI Groq Cloud và lưu trữ đối tượng Cloudflare R2 giúp giảm 60% chi phí hạ tầng.</li>
                            <li>Dẫn dắt đội ngũ 6 kỹ sư, thực hiện Code Review và đảm bảo độ bao phủ kiểm thử > 85%.</li>
                        </ul>
                    </div>

                    <div style=\"margin-bottom: 15px;\">
                        <div style=\"display: flex; justify-content: space-between;\">
                            <strong style=\"font-size: 11.5pt;\">Backend Java Developer</strong>
                            <span style=\"color: #78909c; font-size: 10pt;\">06/2021 – 02/2023</span>
                        </div>
                        <div style=\"color: #1e88e5; font-style: italic; font-size: 10.5pt; margin-bottom: 5px;\">Tập đoàn Giải pháp Số XYZ</div>
                        <ul style=\"margin: 0; padding-left: 20px; font-size: 10.5pt; color: #37474f;\">
                            <li>Xây dựng và bảo trì hơn 30 RESTful APIs phục vụ nền tảng Thương mại Điện tử B2B.</li>
                            <li>Tích hợp cổng thanh toán trực tuyến (VNPay, Momo, Stripe) với cơ chế Idempotency an toàn.</li>
                        </ul>
                    </div>
                </div>

                <!-- Dự án nổi bật -->
                <div style=\"margin-bottom: 20px;\">
                    <h3 style=\"margin: 0 0 10px 0; font-size: 13pt; color: #1976d2; border-bottom: 1px solid #b0bec5; padding-bottom: 4px; text-transform: uppercase;\">DỰ ÁN TIÊU BIỂU</h3>
                    <div style=\"margin-bottom: 10px;\">
                        <strong style=\"font-size: 11pt;\">Cover Letter & CV AI Platform</strong> (2024 - 2025)<br>
                        <span style=\"font-size: 10pt; color: #607d8b;\">Tech stack: React 18, Vite, Spring Boot 3, MySQL, Cloudflare R2, Groq LPU</span>
                        <p style=\"margin: 4px 0 0 0; font-size: 10.5pt;\">Nền tảng tạo hồ sơ xin việc thông minh với tính năng tự động xuất bản file PDF nhị phân tải trực tiếp, hỗ trợ hơn 10.000 người dùng hàng tháng.</p>
                    </div>
                </div>
            </div>

            <!-- Cột Phải -->
            <div style=\"background: #f8f9fa; padding: 15px; border-radius: 6px;\">
                <!-- Kỹ năng -->
                <div style=\"margin-bottom: 20px;\">
                    <h3 style=\"margin: 0 0 8px 0; font-size: 12pt; color: #1976d2; border-bottom: 1px solid #cfd8dc; padding-bottom: 4px; text-transform: uppercase;\">KỸ NĂNG CHUYÊN MÔN</h3>
                    <p style=\"margin: 4px 0; font-size: 10.5pt;\"><strong>Ngôn ngữ:</strong> Java 21, JavaScript (ES6+), TypeScript, SQL</p>
                    <p style=\"margin: 4px 0; font-size: 10.5pt;\"><strong>Backend:</strong> Spring Boot 3, Spring Security, Hibernate JPA, RESTful API</p>
                    <p style=\"margin: 4px 0; font-size: 10.5pt;\"><strong>Frontend:</strong> React.js, Vite, Material UI, Redux / Context API</p>
                    <p style=\"margin: 4px 0; font-size: 10.5pt;\"><strong>Database:</strong> MySQL, PostgreSQL, Redis</p>
                    <p style=\"margin: 4px 0; font-size: 10.5pt;\"><strong>DevOps:</strong> Docker, Git, Nginx, CI/CD GitHub Actions</p>
                </div>

                <!-- Học vấn -->
                <div style=\"margin-bottom: 20px;\">
                    <h3 style=\"margin: 0 0 8px 0; font-size: 12pt; color: #1976d2; border-bottom: 1px solid #cfd8dc; padding-bottom: 4px; text-transform: uppercase;\">HỌC VẤN</h3>
                    <strong style=\"font-size: 10.5pt;\">Đại học Bách Khoa Hà Nội</strong>
                    <p style=\"margin: 2px 0; font-size: 10pt; color: #546e7a;\">Kỹ sư Công nghệ Thông tin</p>
                    <span style=\"font-size: 9.5pt; color: #78909c;\">2017 – 2022 &nbsp;|&nbsp; GPA: 3.4/4.0</span>
                </div>

                <!-- Chứng chỉ -->
                <div style=\"margin-bottom: 20px;\">
                    <h3 style=\"margin: 0 0 8px 0; font-size: 12pt; color: #1976d2; border-bottom: 1px solid #cfd8dc; padding-bottom: 4px; text-transform: uppercase;\">CHỨNG CHỈ</h3>
                    <p style=\"margin: 4px 0; font-size: 10pt;\">🏆 <strong>Oracle Certified Professional:</strong> Java SE 17 Developer</p>
                    <p style=\"margin: 4px 0; font-size: 10pt;\">🏆 <strong>AWS Certified Solutions Architect</strong> – Associate</p>
                    <p style=\"margin: 4px 0; font-size: 10pt;\">📜 <strong>TOEIC:</strong> 850/990</p>
                </div>
            </div>
        </div>
    </div>',
    'https://images.unsplash.com/photo-1586281380349-632531db7ed4?w=400&q=80',
    215,
    'active',
    NOW()
),
(
    'Modern Executive - Quản Trị Kinh Doanh & Lãnh Đạo',
    'Kinh doanh / Quản trị',
    '<div style=\"font-family: Arial, sans-serif; color: #1a1a1a; line-height: 1.6; padding: 25px; background: #ffffff; max-width: 850px; margin: auto;\">
        <!-- Header màu Navy -->
        <div style=\"background: #0d233a; color: #ffffff; padding: 25px; border-radius: 4px; margin-bottom: 20px;\">
            <h1 style=\"margin: 0; font-size: 24pt; letter-spacing: 2px; text-transform: uppercase;\">TRẦN MINH TUẤN</h1>
            <p style=\"margin: 6px 0 0 0; font-size: 13pt; color: #64b5f6;\">CHUYÊN VIÊN PHÁT TRIỂN KINH DOANH QUỐC TẾ (B2B)</p>
            <div style=\"margin-top: 15px; font-size: 10pt; color: #cfd8dc; display: flex; gap: 20px;\">
                <span>📞 (+84) 912 345 678</span>
                <span>✉️ minhtuan.tran@business.com</span>
                <span>📍 Quận 1, TP. Hồ Chí Minh</span>
            </div>
        </div>

        <div style=\"margin-bottom: 20px;\">
            <h3 style=\"color: #0d233a; border-left: 4px solid #0d233a; padding-left: 10px; margin: 0 0 10px 0;\">TÓM TẮT NĂNG LỰC</h3>
            <p style=\"margin: 0; font-size: 11pt;\">Hơn 5 năm kinh nghiệm trong lĩnh vực mở rộng thị trường và quản lý chuỗi cung ứng khu vực Đông Nam Á. Kỹ năng đàm phán hợp đồng thương mại xuất sắc, thành thạo xây dựng quan hệ đối tác chiến lược và đạt mức tăng trưởng doanh số bình quân 35%/năm.</p>
        </div>

        <div style=\"margin-bottom: 20px;\">
            <h3 style=\"color: #0d233a; border-left: 4px solid #0d233a; padding-left: 10px; margin: 0 0 10px 0;\">KINH NGHIỆM THỰC CHIẾN</h3>
            <div style=\"margin-bottom: 15px;\">
                <div style=\"display: flex; justify-content: space-between;\">
                    <strong>Senior Business Development Manager</strong>
                    <span style=\"color: #757575;\">2021 – Nay</span>
                </div>
                <div style=\"color: #1565c0; font-style: italic; margin-bottom: 5px;\">Global Trade Corporation</div>
                <ul style=\"margin: 0; padding-left: 20px; font-size: 10.5pt;\">
                    <li>Ký kết 15 hợp đồng phân phối độc quyền tại thị trường Singapore và Thái Lan, đóng góp 3.2 triệu USD doanh thu năm 2024.</li>
                    <li>Xây dựng chiến lược giá cạnh tranh và quản lý đội ngũ 8 chuyên viên phát triển kinh doanh.</li>
                </ul>
            </div>
        </div>

        <div>
            <h3 style=\"color: #0d233a; border-left: 4px solid #0d233a; padding-left: 10px; margin: 0 0 10px 0;\">KỸ NĂNG VÀ HỌC VẤN</h3>
            <p><strong>Kỹ năng:</strong> Đàm phán B2B, Phân tích Thị trường, Quản lý Quan hệ Khách hàng (HubSpot, Salesforce), Tiếng Anh C1 (IELTS 7.5).</p>
            <p><strong>Học vấn:</strong> Cử nhân Kinh tế Quốc tế - Đại học Ngoại Thương TP.HCM (2015 - 2019).</p>
        </div>
    </div>',
    'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&q=80',
    180,
    'active',
    NOW()
),
(
    'Modern Creative - Sáng Tạo & Thiết Kế UI/UX',
    'Thiết kế / Sáng tạo',
    '<div style=\"font-family: ''Segoe UI'', Tahoma, Geneva, Verdana, sans-serif; color: #2d3748; line-height: 1.6; padding: 25px; background: #ffffff; max-width: 850px; margin: auto; border: 1px solid #edf2f7;\">
        <div style=\"text-align: center; border-bottom: 3px solid #805ad5; padding-bottom: 20px; margin-bottom: 25px;\">
            <h1 style=\"margin: 0; font-size: 26pt; color: #6b46c1; text-transform: uppercase;\">LÊ HOÀNG YẾN</h1>
            <h3 style=\"margin: 5px 0 0 0; font-size: 14pt; color: #718096; font-weight: normal;\">SENIOR PRODUCT & UI/UX DESIGNER</h3>
            <p style=\"margin: 8px 0 0 0; font-size: 10.5pt; color: #a0aec0;\">yenle.design@gmail.com &nbsp;•&nbsp; (+84) 901 234 567 &nbsp;•&nbsp; behance.net/yenledesign</p>
        </div>

        <div style=\"margin-bottom: 25px;\">
            <h3 style=\"color: #6b46c1; margin: 0 0 10px 0;\">GIỚI THIỆU</h3>
            <p style=\"margin: 0; font-size: 11pt;\">Nhà thiết kế sản phẩm số với 4 năm kinh nghiệm tạo ra các trải nghiệm người dùng trực quan, bắt mắt và lấy dữ liệu làm trung tâm. Thành thạo nghiên cứu người dùng, xây dựng Design System quy mô lớn trên Figma và phối hợp nhịp nhàng với đội ngũ lập trình viên.</p>
        </div>

        <div style=\"margin-bottom: 25px;\">
            <h3 style=\"color: #6b46c1; margin: 0 0 15px 0;\">KINH NGHIỆM LÀM VIỆC</h3>
            <div style=\"margin-bottom: 15px;\">
                <div style=\"display: flex; justify-content: space-between;\">
                    <strong>Lead UI/UX Designer</strong>
                    <span style=\"color: #a0aec0;\">2022 – Hiện tại</span>
                </div>
                <div style=\"color: #805ad5; font-style: italic;\">FinTech Innovation Studio</div>
                <ul style=\"margin: 5px 0 0 0; padding-left: 20px; font-size: 10.5pt;\">
                    <li>Thiết kế lại toàn bộ ứng dụng Ngân hàng số, giúp cải thiện 30% chỉ số Net Promoter Score (NPS).</li>
                    <li>Xây dựng Design System đồng nhất với hơn 500 components được tái sử dụng trong cả hệ sinh thái Web & Mobile.</li>
                </ul>
            </div>
        </div>

        <div>
            <h3 style=\"color: #6b46c1; margin: 0 0 10px 0;\">CÔNG CỤ THÀNH THẠO</h3>
            <p style=\"margin: 0; font-size: 11pt;\">Figma, Adobe XD, Illustrator, Photoshop, Principle, Protopie, HTML/CSS cơ bản.</p>
        </div>
    </div>',
    'https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=400&q=80',
    142,
    'active',
    NOW()
);
