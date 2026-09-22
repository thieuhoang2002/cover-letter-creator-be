package cover.letter.creator.dto;

import lombok.Data;

@Data
public class EducationDTO {
    private Integer id;
    private String school;
    private String degree;
    private String fieldOfStudy;
    private String time;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}