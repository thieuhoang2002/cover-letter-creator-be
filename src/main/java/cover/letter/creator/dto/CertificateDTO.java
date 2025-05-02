package cover.letter.creator.dto;

import lombok.Data;

@Data
public class CertificateDTO {
    private Integer id;
    private String name;
    private String issuer;
    private String issueDate;
}