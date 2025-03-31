package cover.letter.creator.service;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;

import cover.letter.creator.model.User;
import cover.letter.creator.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
public class PdfService {

	@Autowired
	private UserRepository userRepository;
	
    public byte[] generatePdfFromHtml(String htmlContent) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            System.out.println("HTML nhận được: " + htmlContent);

            // Tạo ConverterProperties để cấu hình font
            ConverterProperties properties = new ConverterProperties();
            FontProvider fontProvider = new FontProvider();

            // Tải các biến thể của font Times New Roman từ resources
            String[] fontFiles = {
                "/fonts/Times_New_Roman.ttf",          // Regular
                "/fonts/Times_New_Roman_Bold.ttf",     // Bold
                "/fonts/Times_New_Roman_Italic.ttf",   // Italic
                "/fonts/Times_New_Roman_Bold_Italic.ttf" // Bold Italic
            };

            for (String fontPath : fontFiles) {
                try (InputStream fontStream = getClass().getResourceAsStream(fontPath)) {
                    if (fontStream == null) {
                        System.err.println("Không tìm thấy file font: " + fontPath);
                        continue; // Bỏ qua nếu font không tồn tại
                    }
                    FontProgram fontProgram = FontProgramFactory.createFont(fontStream.readAllBytes());
                    fontProvider.addFont(fontProgram);
                }
            }

            // Đặt font mặc định là Times New Roman
            properties.setFontProvider(fontProvider);
            properties.setBaseUri(""); // Để xử lý các đường dẫn tương đối nếu có

            // Chuyển đổi HTML thành PDF với font đã cấu hình
            HtmlConverter.convertToPdf(htmlContent, baos, properties);

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF: " + e.getMessage(), e);
        }
    }
}