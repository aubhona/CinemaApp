package cinema.services;

public enum Color {
    GRAY("\033[0m"),
    GREEN("\033[0;32m"),
    RED("\033[0;31m"),
    YELLOW("\033[0;33m");

    private final String _value;

    Color(String value) {
        _value = value;
    }

    public String getValue() {
        return _value;
    }
}