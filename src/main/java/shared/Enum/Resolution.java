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

    public int getIntResolution() {
        switch (this) {
            case P240:
                return 240;
            case P360:
                return 360;
            case P480:
                return 480;
            case P720:
                return 720;
            case P1080:
                return 1080;
            default:
                throw new IllegalArgumentException("Unknown resolution: " + this);
        }
    }
}
