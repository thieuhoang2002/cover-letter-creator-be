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

    public Map<String, Object> getUserData() { return userData; }
    public void setUserData(Map<String, Object> userData) { this.userData = userData; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getLayout() { return layout; }
    public void setLayout(String layout) { this.layout = layout; }

    public String getFont() { return font; }
    public void setFont(String font) { this.font = font; }

    public String getStyles() { return styles; }
    public void setStyles(String styles) { this.styles = styles; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getResponseFormat() { return responseFormat; }
    public void setResponseFormat(String responseFormat) { this.responseFormat = responseFormat; }

    public List<String> getPlaceholders() { return placeholders; }
    public void setPlaceholders(List<String> placeholders) { this.placeholders = placeholders; }
}