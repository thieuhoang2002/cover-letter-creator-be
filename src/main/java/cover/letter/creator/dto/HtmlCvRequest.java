package cover.letter.creator.dto;

import java.util.List;

public class HtmlCvRequest {
    private String action;
    private String theme;
    private List<String> placeholders;
    private String layout;
    private String font;
    private String styles;
    private String response_format;
    private String position;

    // Getters and Setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public List<String> getPlaceholders() { return placeholders; }
    public void setPlaceholders(List<String> placeholders) { this.placeholders = placeholders; }

    public String getLayout() { return layout; }
    public void setLayout(String layout) { this.layout = layout; }

    public String getFont() { return font; }
    public void setFont(String font) { this.font = font; }

    public String getStyles() { return styles; }
    public void setStyles(String styles) { this.styles = styles; }

    public String getResponse_format() { return response_format; }
    public void setResponse_format(String response_format) { this.response_format = response_format; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}
