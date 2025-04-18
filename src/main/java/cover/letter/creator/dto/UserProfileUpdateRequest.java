package cover.letter.creator.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserProfileUpdateRequest {
    private String name;
    private String email;
    private String avatarUrl;
    private Date birthday;
    private String address;
    private String phone;
    private String school;
    //chuyên ngành
    private String specialization;
}
