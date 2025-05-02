package cover.letter.creator.dto;

import lombok.Data;

@Data
public class ExperienceDTO {
    private Integer id;
    private String company;
    private String role;
    private String time;
    private String description;
}