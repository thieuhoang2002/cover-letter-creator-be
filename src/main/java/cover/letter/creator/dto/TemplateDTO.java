package cover.letter.creator.dto;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TemplateDTO {
    private Integer id;
    private String name;
    private String type;
    private String content;
    private String image;
    private Integer views;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;
    
    private String status;
    private boolean isFavorite;
}