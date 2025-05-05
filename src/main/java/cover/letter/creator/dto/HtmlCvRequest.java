package cover.letter.creator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class HtmlCvRequest {

    @JsonProperty("userData")
    private Map<String, Object> userData;

    @JsonProperty("position")
    private String position;

    @JsonProperty("layout")
    private String layout;

    @JsonProperty("font")
    private String font;

    @JsonProperty("styles")
    private String styles;

    @JsonProperty("theme")
    private String theme;

    @JsonProperty("response_format")
    private String responseFormat;

    @JsonProperty("placeholders")
    private List<String> placeholders;
}