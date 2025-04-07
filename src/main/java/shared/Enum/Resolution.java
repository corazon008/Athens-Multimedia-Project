package shared.Enum;

public enum Resolution {
    P240("240p"),
    P360("360p"),
    P480("480p"),
    P720("720p"),
    P1080("1080p");

    private final String label;

    // Constructeur privé
    Resolution(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
