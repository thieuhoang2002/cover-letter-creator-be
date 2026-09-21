-- ==============================================================================
-- SEED DATA: COVER LETTER & MODERN CV TEMPLATES (NHÀ NƯỚC & HIỆN ĐẠI)
-- ==============================================================================

USE cover_letter_creator_db;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. BẢNG TEMPLATES (COVER LETTER - ĐƠN XIN VIỆC: 3 MẪU NHÀ NƯỚC + 3 MẪU HIỆN ĐẠI)
DELETE FROM templates;

INSERT INTO templates (name, type, content, image, views, status, update_date) VALUES
(
    'Đơn Đăng Ký Dự Tuyển Viên Chức Nhà Nước (Nghị định 115/2020)',
    'Nhà nước / Hành chính',
    '<div style="font-family: ''Times New Roman'', Times, serif; font-size: 14pt; line-height: 1.5; color: #000; max-width: 800px; margin: 0 auto; padding: 30px; text-align: justify;">
    <div style="text-align: center; margin-bottom: 25px;">
        <p style="margin: 0; font-weight: bold; font-size: 13pt; text-transform: uppercase;">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</p>
        <p style="margin: 4px 0 0 0; font-weight: bold; font-size: 14pt;">Độc lập – Tự do – Hạnh phúc</p>
        <div style="width: 180px; height: 1.5px; background: #000; margin: 8px auto 0 auto;"></div>
    </div>

    <div style="text-align: center; margin-bottom: 25px;">
        <h2 style="margin: 0; text-transform: uppercase; font-size: 16pt; font-weight: bold;">ĐƠN ĐĂNG KÝ DỰ TUYỂN VIÊN CHỨC</h2>
        <p style="font-style: italic; margin-top: 5px; font-size: 13pt;">(Ban hành kèm theo Nghị định số 115/2020/NĐ-CP của Chính phủ)</p>
    </div>

    <p style="text-align: center; margin-bottom: 20px;"><strong>Kính gửi:</strong> Hội đồng tuyển dụng viên chức [Tên Cơ quan / Đơn vị sự nghiệp công lập]</p>

    <p>Tôi tên là: <strong>[HỌ VÀ TÊN ỨNG VIÊN]</strong> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Nam/Nữ: [Nam/Nữ]</p>
    <p>Sinh ngày: [Ngày] tháng [Tháng] năm [Năm]</p>
    <p>Quê quán: [Xã/Phường, Huyện/Quận, Tỉnh/Thành phố]</p>
    <p>Hộ khẩu thường trú: [Số nhà, Đường, Phường/Xã, Quận/Huyện, Tỉnh/Thành phố]</p>
    <p>Chỗ ở hiện nay: [Địa chỉ liên lạc hiện tại]</p>
    <p>Số CCCD/CMND: [Số CCCD] &nbsp;|&nbsp; Ngày cấp: [Ngày cấp] &nbsp;|&nbsp; Nơi cấp: [Cục CSQLHC về TTXH]</p>
    <p>Số điện thoại liên lạc: [Số điện thoại] &nbsp;|&nbsp; Email: [Địa chỉ Email]</p>

    <p style="font-weight: bold; margin-top: 15px;">Trình độ đào tạo:</p>
    <p>- Trình độ văn hóa: 12/12</p>
    <p>- Trình độ chuyên môn: [Cử nhân / Thạc sĩ / Kỹ sư] chuyên ngành [Tên Chuyên Ngành], tốt nghiệp Trường [Tên Trường Đại học], hệ đào tạo: [Chính quy], xếp loại: [Giỏi/Khá].</p>
    <p>- Trình độ ngoại ngữ: [Bậc 3 VSTEP / IELTS 6.5 / Tiếng Anh B1]</p>
    <p>- Trình độ tin học: [Chuẩn kỹ năng sử dụng CNTT cơ bản theo Thông tư 03/2014/TT-BTTTT]</p>

    <p>Sau khi nghiên cứu thông báo tuyển dụng viên chức của Quý cơ quan, tôi nhận thấy bản thân có đủ tiêu chuẩn, điều kiện và năng lực phù hợp với vị trí việc làm: <strong>[Tên Vị trí tuyển dụng / Chức danh nghề nghiệp]</strong> tại <strong>[Tên Phòng / Ban / Đơn vị trực thuộc]</strong>.</p>

    <p>Tôi làm đơn này xin được đăng ký dự tuyển vào vị trí việc làm nêu trên. Nếu trúng tuyển, tôi xin chấp hành nghiêm chỉnh mọi sự phân công công tác của tổ chức, thực hiện đúng các quy định của Luật Viên chức và nội quy, quy chế của Quý cơ quan.</p>

    <p>Tôi xin cam đoan hồ sơ đăng ký dự tuyển của tôi là hoàn toàn đúng sự thật. Nếu có điều gì sai sót, tôi xin chịu hoàn toàn trách nhiệm trước pháp luật.</p>

    <div style="margin-top: 35px; display: flex; justify-content: space-between;">
        <div style="width: 45%;"></div>
        <div style="width: 50%; text-align: center;">
            <p style="margin: 0; font-style: italic;">[Địa danh], ngày [DD] tháng [MM] năm [YYYY]</p>
            <p style="margin: 8px 0 65px 0; font-weight: bold; text-transform: uppercase;">Người làm đơn</p>
            <p style="margin: 0; font-weight: bold;">[Họ và Tên Ứng viên]</p>
        </div>
    </div>
</div>',
    'https://images.unsplash.com/photo-1450133064473-71024230f91b?w=400&q=80',
    240,
    'active',
    NOW()
),
(
    'Đơn Xin Chuyển Công Tác Cơ Quan Hành Chính Nhà Nước',
    'Nhà nước / Hành chính',
    '<div style="font-family: ''Times New Roman'', Times, serif; font-size: 14pt; line-height: 1.5; color: #000; max-width: 800px; margin: 0 auto; padding: 30px; text-align: justify;">
    <div style="text-align: center; margin-bottom: 25px;">
        <p style="margin: 0; font-weight: bold; font-size: 13pt; text-transform: uppercase;">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</p>
        <p style="margin: 4px 0 0 0; font-weight: bold; font-size: 14pt;">Độc lập – Tự do – Hạnh phúc</p>
        <div style="width: 180px; height: 1.5px; background: #000; margin: 8px auto 0 auto;"></div>
    </div>

    <div style="text-align: center; margin-bottom: 25px;">
        <h2 style="margin: 0; text-transform: uppercase; font-size: 16pt; font-weight: bold;">ĐƠN XIN CHUYỂN CÔNG TÁC</h2>
        <p style="font-style: italic; margin-top: 5px; font-size: 13pt;">(Về cơ quan, đơn vị hành chính sự nghiệp mới)</p>
    </div>

    <p style="text-align: center; margin-bottom: 15px;">
        <strong>Kính gửi:</strong><br>
        - Lãnh đạo Sở / Ban / Ngành [Tên Cơ quan nơi đến]<br>
        - Thủ trưởng Đơn vị [Tên Cơ quan / Đơn vị nơi đang công tác]
    </p>

    <p>Tôi tên là: <strong>[HỌ VÀ TÊN CÁN BỘ / CÔNG CHỨC]</strong></p>
    <p>Sinh ngày: [DD/MM/YYYY] &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Giới tính: [Nam/Nữ]</p>
    <p>Chức vụ, chức danh nghề nghiệp hiện tại: [Chuyên viên / Kế toán viên / Giảng viên]</p>
    <p>Đơn vị công tác hiện nay: [Tên Cơ quan, Phòng ban hiện đang làm việc]</p>
    <p>Ngày vào biên chế chính thức: [DD/MM/YYYY] &nbsp;|&nbsp; Hệ số lương hiện hưởng: [Hệ số lương]</p>
    <p>Trình độ chuyên môn: [Thạc sĩ / Cử nhân] chuyên ngành [Tên Ngành đào tạo]</p>

    <p><strong>Lý do xin chuyển công tác:</strong></p>
    <p>Trong suốt thời gian [Số năm] năm công tác tại [Tên Cơ quan hiện tại], tôi luôn nỗ lực hoàn thành tốt mọi nhiệm vụ chính trị, chuyên môn được giao, chấp hành nghiêm đường lối của Đảng và pháp luật của Nhà nước.</p>
    <p>Nay do điều kiện [hoàn cảnh gia đình chuyển nơi cư trú / nguyện vọng cống hiến chuyên môn sâu phù hợp với định hướng phát triển của Quý cơ quan mới], tôi làm đơn này kính xin Lãnh đạo Quý cơ quan xem xét, tạo điều kiện cho tôi được thuyên chuyển và tiếp nhận về công tác tại: <strong>[Tên Cơ quan, Đơn vị xin chuyển đến]</strong>.</p>

    <p>Nếu được tiếp nhận về công tác tại đơn vị mới, tôi xin cam đoan:</p>
    <p>1. Bàn giao đầy đủ, chuẩn xác toàn bộ hồ sơ, tài liệu, tài sản của đơn vị cũ trước khi rời vị trí.</p>
    <p>2. Chấp hành tuyệt đối sự phân công, điều động của Lãnh đạo cơ quan mới, nêu cao tinh thần trách nhiệm của người cán bộ, công chức, viên chức tận tụy phục vụ nhân dân.</p>

    <p>Kính mong Lãlength đạo các cấp xem xét và chấp thuận nguyện vọng của tôi.</p>
    <p>Xin trân trọng cảm ơn!</p>

    <div style="margin-top: 35px; display: flex; justify-content: space-between;">
        <div style="width: 45%; text-align: center;">
            <p style="margin: 0; font-weight: bold; text-transform: uppercase;">Ý kiến của Đơn vị nơi đi</p>
            <p style="margin: 5px 0 65px 0; font-style: italic;">(Ký, ghi rõ họ tên và đóng dấu)</p>
        </div>
        <div style="width: 50%; text-align: center;">
            <p style="margin: 0; font-style: italic;">[Địa danh], ngày [DD] tháng [MM] năm [YYYY]</p>
            <p style="margin: 8px 0 65px 0; font-weight: bold; text-transform: uppercase;">Người làm đơn</p>
            <p style="margin: 0; font-weight: bold;">[Họ và Tên Cán bộ]</p>
        </div>
    </div>
</div>',
    'https://images.unsplash.com/photo-1521791136064-7986c2920216?w=400&q=80',
    185,
    'active',
    NOW()
),
(
    'Đơn Xin Dự Tuyển Giáo Viên / Giảng Viên Công Lập',
    'Nhà nước / Giáo dục',
    '<div style="font-family: ''Times New Roman'', Times, serif; font-size: 14pt; line-height: 1.5; color: #000; max-width: 800px; margin: 0 auto; padding: 30px; text-align: justify;">
    <div style="text-align: center; margin-bottom: 25px;">
        <p style="margin: 0; font-weight: bold; font-size: 13pt; text-transform: uppercase;">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</p>
        <p style="margin: 4px 0 0 0; font-weight: bold; font-size: 14pt;">Độc lập – Tự do – Hạnh phúc</p>
        <div style="width: 180px; height: 1.5px; background: #000; margin: 8px auto 0 auto;"></div>
    </div>

    <div style="text-align: center; margin-bottom: 25px;">
        <h2 style="margin: 0; text-transform: uppercase; font-size: 16pt; font-weight: bold;">ĐƠN XIN DỰ TUYỂN GIÁO VIÊN</h2>
        <p style="font-style: italic; margin-top: 5px; font-size: 13pt;">(Vào các cơ sở giáo dục mầm non, phổ thông công lập)</p>
    </div>

    <p style="text-align: center; margin-bottom: 20px;">
        <strong>Kính gửi:</strong> Hội đồng tuyển dụng viên chức ngành Giáo dục và Đào tạo [Huyện/Thị xã/Tỉnh]<br>
        Ban Giám hiệu Trường [Tên Trường Đăng Ký Dự Tuyển]
    </p>

    <p>Tôi tên là: <strong>[HỌ VÀ TÊN ỨNG VIÊN]</strong></p>
    <p>Sinh ngày: [DD/MM/YYYY] &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Dân tộc: [Kinh]</p>
    <p>Địa chỉ thường trú: [Số nhà, Xã/Phường, Huyện/Quận, Tỉnh/Thành phố]</p>
    <p>Số điện thoại: [Số điện thoại] &nbsp;|&nbsp; Email: [Địa chỉ Email]</p>

    <p><strong>Quá trình đào tạo sư phạm:</strong></p>
    <p>- Tốt nghiệp Trường: [Trường Đại học Sư phạm / Khoa Sư phạm - ĐHQG]</p>
    <p>- Chuyên ngành: Sư phạm [Toán học / Ngữ văn / Tiếng Anh / Tin học]</p>
    <p>- Xếp loại tốt nghiệp: [Giỏi / Xuất sắc] &nbsp;|&nbsp; Năm tốt nghiệp: [Năm]</p>
    <p>- Chứng chỉ bồi dưỡng nghiệp vụ sư phạm / Chuẩn chức danh nghề nghiệp giáo viên hạng III.</p>

    <p>Qua nghiên cứu kế hoạch và chỉ tiêu tuyển dụng giáo viên của Nhà trường, tôi nhận thấy phẩm chất đạo đức, kiến thức chuyên môn và lòng nhiệt huyết với sự nghiệp trồng người của bản thân hoàn toàn phù hợp với vị trí tuyển dụng: <strong>Giáo viên môn [Tên Môn học]</strong>.</p>

    <p>Nếu vinh dự được trúng tuyển vào đội ngũ nhà giáo của Quý trường, tôi xin hứa:</p>
    <p>1. Tận tâm, gương mẫu, chấp hành nghiêm Điều lệ nhà trường và Quy định đạo đức nhà giáo.</p>
    <p>2. Không ngừng đổi mới phương pháp giảng dạy, ứng dụng công nghệ thông tin và chuyển đổi số trong giáo dục để nâng cao chất lượng bài giảng.</p>
    <p>3. Hoàn thành xuất sắc mọi nhiệm vụ giáo dục do Ban Giám hiệu phân công.</p>

    <p>Kính mong Hội đồng tuyển dụng xem xét và cho phép tôi được tham dự kỳ thi tuyển.</p>

    <div style="margin-top: 35px; display: flex; justify-content: space-between;">
        <div style="width: 45%;"></div>
        <div style="width: 50%; text-align: center;">
            <p style="margin: 0; font-style: italic;">[Địa danh], ngày [DD] tháng [MM] năm [YYYY]</p>
            <p style="margin: 8px 0 65px 0; font-weight: bold; text-transform: uppercase;">Người làm đơn</p>
            <p style="margin: 0; font-weight: bold;">[Họ và Tên Ứng viên]</p>
        </div>
    </div>
</div>',
    'https://images.unsplash.com/photo-1524178232363-1fb2b075b655?w=400&q=80',
    195,
    'active',
    NOW()
),
(
    'Đơn Xin Việc - Kỹ Sư Phần Mềm (Software Engineer)',
    'Hiện đại / Công nghệ',
    '<div style="font-family: Arial, sans-serif; font-size: 13pt; line-height: 1.6; color: #333; max-width: 800px; margin: 0 auto; padding: 25px;">
    <div style="border-left: 4px solid #1976d2; padding-left: 15px; margin-bottom: 25px;">
        <h2 style="margin: 0; text-transform: uppercase; color: #1976d2; font-size: 18pt;">COVER LETTER</h2>
        <p style="font-weight: bold; margin: 4px 0 0 0; color: #555;">ỨNG TUYỂN: SENIOR FULLSTACK DEVELOPER / SOFTWARE ENGINEER</p>
    </div>

    <div style="background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 12px 16px; margin-bottom: 20px; font-size: 12pt;">
        <strong>Ứng viên:</strong> [Họ và Tên của Bạn] &nbsp;|&nbsp; <strong>Email:</strong> [your.email@gmail.com] &nbsp;|&nbsp; <strong>SĐT:</strong> [0901 234 567]<br>
        <strong>Portfolio/GitHub:</strong> github.com/yourusername &nbsp;|&nbsp; <strong>Địa chỉ:</strong> Hà Nội / TP. Hồ Chí Minh
    </div>

    <p><strong>Kính gửi:</strong> Ban Tuyển dụng & Đội ngũ Kỹ thuật Công ty <em>[Tên Công ty Công nghệ]</em>,</p>

    <p>Tôi viết đơn này bày tỏ sự hào hứng mãnh liệt khi ứng tuyển vào vị trí <strong>Senior Software Engineer</strong> tại Quý công ty. Với hơn 3 năm kinh nghiệm thực chiến phát triển các giải pháp phần mềm quy mô lớn dựa trên nền tảng <strong>Java Spring Boot, ReactJS, Docker</strong> và điện toán đám mây, tôi tin tưởng sẽ đóng góp hiệu quả vào sự bứt phá của sản phẩm công nghệ của Quý công ty.</p>

    <p>Tại các dự án trước đây, tôi đã trực tiếp mang lại những giá trị đo lường được:</p>
    <ul style="padding-left: 20px;">
        <li><strong>Kiến trúc Microservices:</strong> Thiết kế và tối ưu hóa hệ thống xử lý hơn 500,000 requests/ngày, giảm 40% chi phí hạ tầng server.</li>
        <li><strong>Tối ưu hóa Database:</strong> Nâng cấp chỉ mục MySQL & caching Redis, cải thiện 50% thời gian phản hồi API (từ 250ms xuống dưới 80ms).</li>
        <li><strong>Quy trình CI/CD Hiện đại:</strong> Tự động hóa kiểm thử và triển khai với Docker, GitHub Actions, rút ngắn thời gian release phiên bản mới.</li>
    </ul>

    <p>Tôi luôn theo đuổi triết lý viết mã sạch (Clean Code), tư duy hướng sản phẩm và tinh thần học hỏi công nghệ liên tục. Tôi rất mong có cơ hội trao đổi trực tiếp với anh/chị trong buổi phỏng vấn kỹ thuật sắp tới.</p>

    <p>Trân trọng cảm ơn Quý công ty!</p>

    <div style="margin-top: 35px; text-align: right;">
        <p style="margin: 0; font-style: italic;">TP. Hồ Chí Minh, ngày [DD] tháng [MM] năm [YYYY]</p>
        <p style="margin: 10px 0 50px 0; font-weight: bold;">Người ứng tuyển</p>
        <p style="margin: 0; font-weight: bold; color: #1976d2;">[Họ và Tên của Bạn]</p>
    </div>
</div>',
    'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&q=80',
    312,
    'active',
    NOW()
),
(
    'Thư Ứng Tuyển - Trưởng Phòng Kinh Doanh & Marketing',
    'Hiện đại / Kinh doanh',
    '<div style="font-family: Arial, sans-serif; font-size: 13pt; line-height: 1.6; color: #2d3748; max-width: 800px; margin: 0 auto; padding: 25px;">
    <div style="display: flex; justify-content: space-between; border-bottom: 2px solid #0d9488; padding-bottom: 15px; margin-bottom: 25px;">
        <div>
            <h2 style="margin: 0; color: #0d9488; text-transform: uppercase;">THƯ ỨNG TUYỂN (COVER LETTER)</h2>
            <p style="margin: 4px 0 0 0; font-weight: bold; color: #4a5568;">VỊ TRÍ: TRƯỞNG PHÒNG PHÁT TRIỂN KINH DOANH & MARKETING</p>
        </div>
    </div>

    <p><strong>Kính gửi:</strong> Ban Giám đốc & Phòng Nhân sự Công ty <em>[Tên Doanh Nghiệp]</em>,</p>

    <p>Tôi rất vinh dự khi được gửi hồ sơ ứng tuyển vị trí <strong>Trưởng phòng Phát triển Kinh doanh & Marketing</strong> tại Quý công ty. Theo dõi bước tiến thần tốc và tầm nhìn chiến lược của công ty trên thị trường thời gian qua, tôi vô cùng ấn tượng và mong muốn đem kinh nghiệm 4 năm dẫn dắt đội ngũ tăng trưởng doanh thu để đồng hành cùng sự phát triển bền vững của doanh nghiệp.</p>

    <p>Một số thành tựu nổi bật trong sự nghiệp của tôi:</p>
    <ul style="padding-left: 20px;">
        <li>Tăng trưởng doanh thu 135% liên tiếp trong 2 năm tài chính, mở rộng 40+ đối tác chiến lược B2B phân khúc khách hàng doanh nghiệp.</li>
        <li>Tái cấu trúc quy trình phễu chuyển đổi (Sales Funnel), giảm 25% chi phí thu hút khách hàng mới (CAC) và tăng 30% tỷ lệ giữ chân khách hàng (Retention Rate).</li>
        <li>Đào tạo, quản lý và truyền cảm hứng cho đội ngũ 15 nhân sự bán hàng và tiếp thị số đạt 100% KPI quý.</li>
    </ul>

    <p>Với kỹ năng đàm phán sắc bén, khả năng phân tích dữ liệu thị trường và phong cách lãnh đạo linh hoạt, tôi tự tin sẽ xây dựng chiến lược kinh doanh đột phá cho các dòng sản phẩm của công ty.</p>

    <p>Xin chân thành cảm ơn Quý công ty đã dành thời gian xem xét thư này!</p>

    <div style="margin-top: 35px; text-align: right;">
        <p style="margin: 0; font-style: italic;">Hà Nội, ngày [DD] tháng [MM] năm [YYYY]</p>
        <p style="margin: 10px 0 50px 0; font-weight: bold;">Kính thư</p>
        <p style="margin: 0; font-weight: bold; color: #0d9488;">[Họ và Tên của Bạn]</p>
    </div>
</div>',
    'https://images.unsplash.com/photo-1557804506-669a67965ba0?w=400&q=80',
    215,
    'active',
    NOW()
),
(
    'Đơn Xin Việc - Kế Toán Trưởng / Kế Toán Tổng Hợp',
    'Hiện đại / Tài chính',
    '<div style="font-family: Arial, sans-serif; font-size: 13pt; line-height: 1.6; color: #1e293b; max-width: 800px; margin: 0 auto; padding: 25px;">
    <div style="border-bottom: 2px solid #334155; padding-bottom: 12px; margin-bottom: 20px;">
        <h2 style="margin: 0; text-transform: uppercase; color: #1e293b; font-size: 17pt;">ĐƠN XIN VIỆC - VỊ TRÍ KẾ TOÁN TRƯỞNG / TỔNG HỢP</h2>
        <p style="margin: 4px 0 0 0; color: #64748b; font-style: italic;">Chuyên sâu: Báo cáo tài chính, Tối ưu thuế & Kiểm soát nội bộ</p>
    </div>

    <p><strong>Kính gửi:</strong> Ban Giám đốc & Phòng Hành chính Nhân sự Công ty <em>[Tên Công Ty]</em>,</p>

    <p>Tôi làm đơn này để ứng tuyển vào vị trí <strong>Kế toán Trưởng / Kế toán Tổng hợp</strong>. Với chứng chỉ Kế toán trưởng, cử nhân Học viện Tài chính và hơn 5 năm kinh nghiệm quản lý hệ thống sổ sách kế toán, lập báo cáo tài chính và quyết toán thuế cho các doanh nghiệp quy mô vừa và lớn, tôi hoàn toàn tự tin đảm nhiệm trọng trách quản trị tài chính tại Quý công ty.</p>

    <p>Các năng lực cốt lõi tôi cam kết mang lại:</p>
    <ul style="padding-left: 20px;">
        <li><strong>Kiểm soát dòng tiền:</strong> Quản lý minh bạch, cân đối dòng tiền kinh doanh, thiết lập hệ thống cảnh báo rủi ro thanh khoản sớm.</li>
        <li><strong>Tuân thủ pháp luật thuế:</strong> Đại diện doanh nghiệp làm việc với Cơ quan Thuế, giải trình số liệu chuẩn xác, tiết kiệm 15% rủi ro phạt hành chính.</li>
        <li><strong>Ứng dụng ERP:</strong> Thành thạo các hệ thống quản trị MISA, SAP, Fast Accounting và chuyển đổi số hóa đơn điện tử toàn diện.</li>
    </ul>

    <p>Tôi rất mong có dịp gặp gỡ trực tiếp để thảo luận chi tiết về phương án tối ưu tài chính cho công ty.</p>
    <p>Xin trân trọng cảm ơn!</p>

    <div style="margin-top: 35px; text-align: right;">
        <p style="margin: 0; font-style: italic;">Ngày [DD] tháng [MM] năm [YYYY]</p>
        <p style="margin: 10px 0 50px 0; font-weight: bold;">Người nộp hồ sơ</p>
        <p style="margin: 0; font-weight: bold; color: #1e293b;">[Họ và Tên của Bạn]</p>
    </div>
</div>',
    'https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=400&q=80',
    178,
    'active',
    NOW()
);

-- 2. BẢNG MODERN_CV_TEMPLATES (MODERN CV: 2 MẪU NHÀ NƯỚC + 3 MẪU HIỆN ĐẠI)
DELETE FROM modern_cv_templates;

INSERT INTO modern_cv_templates (name, type, content, image, views, status, update_date) VALUES
(
    'Sơ Yếu Lý Lịch Chuẩn Cán Bộ - Công Chức - Viên Chức (Mẫu 2C-BNV)',
    'Nhà nước / Cán bộ',
    '<div style="font-family: ''Times New Roman'', Times, serif; font-size: 13pt; line-height: 1.5; color: #000; width: 100%; max-width: 800px; margin: 0 auto; padding: 25px; box-sizing: border-box;">
    <div style="text-align: center; margin-bottom: 20px;">
        <p style="margin: 0; font-weight: bold; font-size: 13pt; text-transform: uppercase;">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</p>
        <p style="margin: 3px 0 0 0; font-weight: bold; font-size: 13pt;">Độc lập – Tự do – Hạnh phúc</p>
        <div style="width: 170px; height: 1.5px; background: #000; margin: 6px auto;"></div>
    </div>

    <div style="text-align: center; margin-bottom: 20px;">
        <h2 style="margin: 0; text-transform: uppercase; font-size: 16pt; font-weight: bold;">SƠ YẾU LÝ LỊCH VIÊN CHỨC</h2>
        <p style="font-style: italic; margin-top: 3px; font-size: 12pt;">(Mẫu chuẩn hồ sơ cán bộ, công chức, viên chức quản lý)</p>
    </div>

    <table style="width: 100%; border-collapse: collapse; margin-bottom: 15px;">
        <tr>
            <td style="width: 130px; text-align: center; border: 1px solid #000; padding: 10px; vertical-align: middle;">
                <div style="width: 100px; height: 130px; border: 1px dashed #666; margin: 0 auto; line-height: 130px; font-size: 11pt; color: #777;">Ảnh 4x6</div>
            </td>
            <td style="padding-left: 20px; vertical-align: top;">
                <p style="margin: 4px 0;"><strong>1. Họ và tên khai sinh:</strong> [HỌ VÀ TÊN VIẾT HOA]</p>
                <p style="margin: 4px 0;"><strong>2. Giới tính:</strong> [Nam/Nữ] &nbsp;&nbsp;&nbsp;&nbsp; <strong>3. Ngày sinh:</strong> [DD/MM/YYYY]</p>
                <p style="margin: 4px 0;"><strong>4. Quê quán:</strong> [Xã/Phường, Huyện/Quận, Tỉnh/Thành phố]</p>
                <p style="margin: 4px 0;"><strong>5. Dân tộc:</strong> [Kinh] &nbsp;&nbsp;&nbsp;&nbsp; <strong>6. Tôn giáo:</strong> [Không]</p>
                <p style="margin: 4px 0;"><strong>7. Số CCCD:</strong> [012345678901] &nbsp;|&nbsp; <strong>Nơi cấp:</strong> [Cục CSQLHC về TTXH]</p>
            </td>
        </tr>
    </table>

    <div style="border-top: 1.5px solid #000; padding-top: 10px; margin-top: 10px;">
        <h3 style="margin: 0 0 8px 0; text-transform: uppercase; font-size: 13pt;">I. TRÌNH ĐỘ HỌC VẤN & CHUYÊN MÔN</h3>
        <p style="margin: 4px 0;">• <strong>Giáo dục phổ thông:</strong> 12/12</p>
        <p style="margin: 4px 0;">• <strong>Học vị cao nhất:</strong> [Thạc sĩ / Cử nhân] - Chuyên ngành: [Quản lý Nhà nước / Luật / CNTT]</p>
        <p style="margin: 4px 0;">• <strong>Cơ sở đào tạo:</strong> [Học viện Hành chính Quốc gia / Đại học Quốc gia Hà Nội]</p>
        <p style="margin: 4px 0;">• <strong>Lý luận chính trị:</strong> [Trung cấp / Cao cấp lý luận chính trị]</p>
        <p style="margin: 4px 0;">• <strong>Quản lý nhà nước:</strong> [Chứng chỉ Bồi dưỡng Quản lý Nhà nước ngạch Chuyên viên chính / Chuyên viên]</p>
        <p style="margin: 4px 0;">• <strong>Ngoại ngữ:</strong> Tiếng Anh Bậc 3 (B1) Khung năng lực ngoại ngữ 6 bậc dùng cho Việt Nam</p>
    </div>

    <div style="border-top: 1.5px solid #000; padding-top: 10px; margin-top: 15px;">
        <h3 style="margin: 0 0 8px 0; text-transform: uppercase; font-size: 13pt;">II. TÓM TẮT QUÁ TRÌNH CÔNG TÁC</h3>
        <table style="width: 100%; border-collapse: collapse; border: 1px solid #000; text-align: center; font-size: 12pt;">
            <thead>
                <tr style="background-color: #f2f2f2;">
                    <th style="border: 1px solid #000; padding: 6px; width: 25%;">Thời gian</th>
                    <th style="border: 1px solid #000; padding: 6px; width: 45%;">Chức danh, chức vụ, đơn vị công tác</th>
                    <th style="border: 1px solid #000; padding: 6px; width: 30%;">Vị trí việc làm</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td style="border: 1px solid #000; padding: 6px;">09/2021 - Nay</td>
                    <td style="border: 1px solid #000; padding: 6px; text-align: left;">Sở / Ban / Ngành [Tên Cơ quan Nhà nước]</td>
                    <td style="border: 1px solid #000; padding: 6px;">Chuyên viên</td>
                </tr>
                <tr>
                    <td style="border: 1px solid #000; padding: 6px;">08/2019 - 08/2021</td>
                    <td style="border: 1px solid #000; padding: 6px; text-align: left;">Đơn vị sự nghiệp công lập [Tên Đơn vị]</td>
                    <td style="border: 1px solid #000; padding: 6px;">Cán bộ hợp đồng</td>
                </tr>
            </tbody>
        </table>
    </div>

    <div style="border-top: 1.5px solid #000; padding-top: 10px; margin-top: 15px;">
        <h3 style="margin: 0 0 8px 0; text-transform: uppercase; font-size: 13pt;">III. KHEN THƯỞNG & KỶ LUẬT</h3>
        <p style="margin: 4px 0;">• <strong>Khen thưởng:</strong> Chiến sĩ thi đua cơ sở năm 2023; Giấy khen của Giám đốc Sở năm 2022.</p>
        <p style="margin: 4px 0;">• <strong>Kỷ luật:</strong> Không.</p>
    </div>

    <div style="margin-top: 30px; display: flex; justify-content: space-between;">
        <div style="width: 45%; text-align: center;">
            <p style="margin: 0; font-weight: bold; text-transform: uppercase;">Xác nhận của Cơ quan</p>
            <p style="margin: 5px 0 60px 0; font-style: italic;">(Ký tên và đóng dấu)</p>
        </div>
        <div style="width: 50%; text-align: center;">
            <p style="margin: 0; font-style: italic;">Ngày [DD] tháng [MM] năm [YYYY]</p>
            <p style="margin: 5px 0 60px 0; font-weight: bold; text-transform: uppercase;">Người khai cam đoan</p>
            <p style="margin: 0; font-weight: bold;">[Họ và Tên Cán bộ]</p>
        </div>
    </div>
</div>',
    'https://images.unsplash.com/photo-1450133064473-71024230f91b?w=400&q=80',
    298,
    'active',
    NOW()
),
(
    'CV Cán Bộ Y Tế / Bác Sĩ / Dược Sĩ Bệnh Viện Công Lập',
    'Nhà nước / Y tế',
    '<div style="font-family: ''Times New Roman'', Times, serif; font-size: 13pt; line-height: 1.5; color: #111; width: 100%; max-width: 800px; margin: 0 auto; padding: 25px; box-sizing: border-box;">
    <div style="border-bottom: 2px solid #0284c7; padding-bottom: 15px; margin-bottom: 20px;">
        <div style="display: flex; justify-content: space-between; align-items: center;">
            <div>
                <h1 style="margin: 0; color: #0284c7; font-size: 20pt; text-transform: uppercase;">BS. [HỌ VÀ TÊN BÁC SĨ]</h1>
                <p style="margin: 5px 0 0 0; font-weight: bold; font-size: 14pt;">BÁC SĨ CHUYÊN KHOA I / NỘI KHOA & HỒI SỨC CẤP CỨU</p>
                <p style="margin: 3px 0 0 0; font-style: italic;">Chứng chỉ hành nghề khám bệnh, chữa bệnh số: [12345/BYT-CCHN]</p>
            </div>
            <div style="width: 90px; height: 110px; border: 1px solid #0284c7; text-align: center; line-height: 110px; font-size: 10pt; color: #555;">Ảnh thẻ 3x4</div>
        </div>
    </div>

    <div style="margin-bottom: 15px;">
        <h3 style="margin: 0 0 6px 0; color: #0284c7; text-transform: uppercase; font-size: 13pt;">I. THÔNG TIN CHUNG</h3>
        <p style="margin: 3px 0;">• Năm sinh: [1992] &nbsp;|&nbsp; Giới tính: [Nam] &nbsp;|&nbsp; Điện thoại: [0912 345 678] &nbsp;|&nbsp; Email: [doctor@hospital.gov.vn]</p>
        <p style="margin: 3px 0;">• Đơn vị công tác hiện tại: Khoa Cấp cứu - Bệnh viện Đa khoa Tỉnh [Tên Tỉnh]</p>
    </div>

    <div style="margin-bottom: 15px;">
        <h3 style="margin: 0 0 6px 0; color: #0284c7; text-transform: uppercase; font-size: 13pt;">II. QUÁ TRÌNH ĐÀO TẠO Y KHOA</h3>
        <p style="margin: 3px 0;">• <strong>2020 - 2022:</strong> Bác sĩ Chuyên khoa I chuyên ngành Nội khoa - Đại học Y Hà Nội (Xếp loại: Giỏi).</p>
        <p style="margin: 3px 0;">• <strong>2010 - 2016:</strong> Bác sĩ Đa khoa hệ chính quy - Đại học Y Dược TP.HCM.</p>
        <p style="margin: 3px 0;">• <strong>Chứng chỉ đào tạo liên tục (CME):</strong> Cấp cứu ngừng tuần hoàn nâng cao (ACLS), Hồi sức cấp cứu ngộ độc cấp.</p>
    </div>

    <div style="margin-bottom: 15px;">
        <h3 style="margin: 0 0 6px 0; color: #0284c7; text-transform: uppercase; font-size: 13pt;">III. KINH NGHIỆM ĐIỀU TRỊ & CHUYÊN MÔN</h3>
        <p style="margin: 3px 0;">• Trực tiếp chẩn đoán, cấp cứu và xử trí thành công hàng trăm ca sốc phản vệ, nhồi máu cơ tim cấp và đa chấn thương.</p>
        <p style="margin: 3px 0;">• Thành thạo các thủ thuật: Đặt nội khí quản khó, đặt catheter tĩnh mạch trung tâm, dẫn lưu màng phổi cấp cứu.</p>
        <p style="margin: 3px 0;">• Tham gia đoàn chi viện phòng chống dịch bệnh và hỗ trợ y tế tuyến huyện vùng sâu, vùng xa.</p>
    </div>

    <div style="margin-bottom: 15px;">
        <h3 style="margin: 0 0 6px 0; color: #0284c7; text-transform: uppercase; font-size: 13pt;">IV. ĐỀ TÀI NGHIÊN CỨU KHOA HỌC</h3>
        <p style="margin: 3px 0;">• <strong>Chủ nhiệm đề tài cấp cơ sở (2023):</strong> "Đánh giá hiệu quả phác đồ kiểm soát đường huyết sớm ở bệnh nhân nhiễm khuẩn huyết nặng tại Khoa Hồi sức tích cực".</p>
    </div>

    <div style="margin-top: 25px; text-align: right;">
        <p style="margin: 0; font-style: italic;">[Địa danh], ngày [DD] tháng [MM] năm [YYYY]</p>
        <p style="margin: 5px 0 50px 0; font-weight: bold;">Người khai</p>
        <p style="margin: 0; font-weight: bold; color: #0284c7;">BS. [Họ và Tên Bác sĩ]</p>
    </div>
</div>',
    'https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?w=400&q=80',
    220,
    'active',
    NOW()
),
(
    'CV Hiện Đại - Kỹ Sư Công Nghệ Thông Tin (Tech Minimalist)',
    'Hiện đại / Công nghệ',
    '<div style="font-family: ''Segoe UI'', Tahoma, Geneva, Verdana, sans-serif; max-width: 800px; margin: 0 auto; background: #ffffff; padding: 30px; box-sizing: border-box; color: #333;">
    <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 3px solid #2563eb; padding-bottom: 20px; margin-bottom: 25px;">
        <div>
            <h1 style="margin: 0; font-size: 24pt; color: #1e293b; text-transform: uppercase; letter-spacing: 1px;">[HỌ VÀ TÊN CỦA BẠN]</h1>
            <h3 style="margin: 6px 0 0 0; color: #2563eb; font-weight: 600; font-size: 14pt;">SENIOR FULLSTACK DEVELOPER</h3>
        </div>
        <div style="text-align: right; font-size: 10pt; color: #64748b; line-height: 1.6;">
            <p style="margin: 0;">📧 dev.pro@email.com</p>
            <p style="margin: 0;">📱 090 123 4567</p>
            <p style="margin: 0;">🌐 github.com/yourprofile</p>
            <p style="margin: 0;">📍 Hà Nội, Việt Nam</p>
        </div>
    </div>

    <div style="margin-bottom: 22px;">
        <h4 style="margin: 0 0 8px 0; color: #1e293b; font-size: 12pt; text-transform: uppercase; border-bottom: 1.5px solid #e2e8f0; padding-bottom: 4px;">MỤC TIÊU NGHỀ NGHIỆP</h4>
        <p style="margin: 0; font-size: 10.5pt; line-height: 1.6; color: #475569;">Kỹ sư phần mềm hơn 4 năm kinh nghiệm với đam mê xây dựng hệ thống web chịu tải cao, microservices và ứng dụng trí tuệ nhân tạo. Mong muốn đồng hành cùng đội ngũ kỹ thuật phát triển sản phẩm công nghệ vươn tầm khu vực.</p>
    </div>

    <div style="margin-bottom: 22px;">
        <h4 style="margin: 0 0 10px 0; color: #1e293b; font-size: 12pt; text-transform: uppercase; border-bottom: 1.5px solid #e2e8f0; padding-bottom: 4px;">KINH NGHIỆM LÀM VIỆC</h4>
        <div style="margin-bottom: 12px;">
            <div style="display: flex; justify-content: space-between; font-size: 11pt; font-weight: bold; color: #0f172a;">
                <span>SENIOR JAVA BACKEND DEVELOPER – VNG TECH</span>
                <span style="color: #2563eb;">2022 - Hiện tại</span>
            </div>
            <p style="margin: 3px 0 0 0; font-style: italic; font-size: 10pt; color: #64748b;">Tech stack: Java 21, Spring Boot 3, Redis, Kafka, Cloudflare R2, MySQL</p>
            <ul style="margin: 5px 0 0 0; padding-left: 18px; font-size: 10pt; line-height: 1.5; color: #334155;">
                <li>Chủ trì thiết kế kiến trúc phân tán phục vụ 2M+ active users hàng tháng.</li>
                <li>Tối ưu hóa hệ thống bộ nhớ đệm đa tầng, giảm tải 65% cho cụm cơ sở dữ liệu.</li>
            </ul>
        </div>
    </div>

    <div style="margin-bottom: 22px;">
        <h4 style="margin: 0 0 10px 0; color: #1e293b; font-size: 12pt; text-transform: uppercase; border-bottom: 1.5px solid #e2e8f0; padding-bottom: 4px;">KỸ NĂNG CÔNG NGHỆ</h4>
        <div style="display: flex; flex-wrap: wrap; gap: 8px; font-size: 10pt;">
            <span style="background: #eff6ff; color: #1d4ed8; padding: 4px 10px; border-radius: 4px; font-weight: bold;">Java / Spring Boot</span>
            <span style="background: #eff6ff; color: #1d4ed8; padding: 4px 10px; border-radius: 4px; font-weight: bold;">React / Next.js</span>
            <span style="background: #eff6ff; color: #1d4ed8; padding: 4px 10px; border-radius: 4px; font-weight: bold;">Docker & Kubernetes</span>
            <span style="background: #eff6ff; color: #1d4ed8; padding: 4px 10px; border-radius: 4px; font-weight: bold;">Cloudflare R2 / AWS</span>
            <span style="background: #eff6ff; color: #1d4ed8; padding: 4px 10px; border-radius: 4px; font-weight: bold;">MySQL & PostgreSQL</span>
        </div>
    </div>
</div>',
    'https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=400&q=80',
    450,
    'active',
    NOW()
),
(
    'CV Hiện Đại - Quản Lý & Kinh Doanh (Corporate Navy)',
    'Hiện đại / Kinh doanh',
    '<div style="font-family: ''Helvetica Neue'', Helvetica, Arial, sans-serif; max-width: 800px; margin: 0 auto; background: #ffffff; padding: 30px; box-sizing: border-box; color: #1a202c;">
    <div style="background: #0f172a; color: #ffffff; padding: 25px; border-radius: 8px; margin-bottom: 25px;">
        <h1 style="margin: 0; font-size: 22pt; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px;">[HỌ VÀ TÊN ỨNG VIÊN]</h1>
        <p style="margin: 5px 0 0 0; color: #38bdf8; font-size: 13pt; font-weight: 500;">HEAD OF BUSINESS DEVELOPMENT & SALES STRATEGY</p>
        <p style="margin: 10px 0 0 0; font-size: 10.5pt; color: #94a3b8;">Email: executive@company.com &nbsp;|&nbsp; Điện thoại: 0988 777 666 &nbsp;|&nbsp; TP. Hồ Chí Minh</p>
    </div>

    <div style="margin-bottom: 20px;">
        <h3 style="margin: 0 0 10px 0; color: #0f172a; font-size: 13pt; text-transform: uppercase; border-left: 4px solid #38bdf8; padding-left: 10px;">TỔNG QUAN NĂNG LỰC</h3>
        <p style="margin: 0; font-size: 10.5pt; line-height: 1.6; color: #334155;">Chuyên gia phát triển kinh doanh với hơn 6 năm dẫn dắt tăng trưởng doanh số khối B2B và xây dựng quan hệ đối tác cấp cao. Thế mạnh đặc thù về đàm phán chiến lược, tái cấu trúc đội ngũ bán hàng và thâm nhập thị trường mới.</p>
    </div>

    <div style="margin-bottom: 20px;">
        <h3 style="margin: 0 0 10px 0; color: #0f172a; font-size: 13pt; text-transform: uppercase; border-left: 4px solid #38bdf8; padding-left: 10px;">THÀNH TÍCH ĐẠT ĐƯỢC</h3>
        <ul style="margin: 0; padding-left: 20px; font-size: 10.5pt; line-height: 1.6; color: #334155;">
            <li>Đạt 140% chỉ tiêu doanh số năm 2023, đem lại hơn 35 tỷ đồng doanh thu thuần cho công ty.</li>
            <li>Xây dựng thành công kênh phân phối độc quyền với 50 đại lý lớn tại thị trường miền Nam.</li>
            <li>Tuyển dụng và huấn luyện đội ngũ 20 Sales Executives đạt tỷ lệ chốt deal trên 38%.</li>
        </ul>
    </div>
</div>',
    'https://images.unsplash.com/photo-1507679799987-c73779587ccf?w=400&q=80',
    310,
    'active',
    NOW()
),
(
    'CV Hiện Đại - Thiết Kế & Sáng Tạo (Creative Emerald)',
    'Hiện đại / Thiết kế',
    '<div style="font-family: ''Montserrat'', sans-serif; max-width: 800px; margin: 0 auto; background: #ffffff; padding: 30px; box-sizing: border-box; color: #1e293b;">
    <div style="border-left: 6px solid #059669; padding-left: 20px; margin-bottom: 25px;">
        <h1 style="margin: 0; font-size: 24pt; font-weight: 800; color: #0f172a;">[HỌ TÊN NHÀ THIẾT KẾ]</h1>
        <h3 style="margin: 4px 0 0 0; color: #059669; font-weight: 600; font-size: 13pt;">SENIOR UI/UX & PRODUCT DESIGNER</h3>
        <p style="margin: 6px 0 0 0; font-size: 10pt; color: #64748b;">Behance.net/portfolio &nbsp;•&nbsp; Dribbble.com/designer &nbsp;•&nbsp; 0933 456 789</p>
    </div>

    <div style="margin-bottom: 20px;">
        <h4 style="margin: 0 0 8px 0; color: #059669; font-size: 11pt; text-transform: uppercase; letter-spacing: 1px;">TRIẾT LÝ THIẾT KẾ</h4>
        <p style="margin: 0; font-size: 10.5pt; line-height: 1.6; color: #334155;">Tập trung tạo ra trải nghiệm người dùng liền mạch, thiết kế trực quan dựa trên dữ liệu người dùng và thẩm mỹ hiện đại. Đã tham gia thiết kế ứng dụng Fintech đạt 1M+ lượt tải trên App Store & Google Play.</p>
    </div>

    <div style="margin-bottom: 20px;">
        <h4 style="margin: 0 0 8px 0; color: #059669; font-size: 11pt; text-transform: uppercase; letter-spacing: 1px;">CÔNG CỤ THÀNH THẠO</h4>
        <p style="margin: 0; font-size: 10.5pt; line-height: 1.6; color: #334155;">Figma, Adobe XD, Illustrator, Photoshop, Principle, Protopie, Design System, Wireframing, Usability Testing.</p>
    </div>
</div>',
    'https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=400&q=80',
    275,
    'active',
    NOW()
);

SET FOREIGN_KEY_CHECKS = 1;
