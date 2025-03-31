package tech.nmhillusion.jCamelDecoderApp.constant;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-03-22
 */
public enum LogType {
    DEBUG("debug"),
    INFO("info"),
    WARN("warn"),
    ERROR("error"),
    ;

    private final String value;

    LogType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
