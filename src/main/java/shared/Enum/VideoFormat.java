package shared.Enum;

import java.io.Serializable;

public enum VideoFormat implements Serializable {
    AVI("avi"),
    MP4("mp4"),
    MKV("mkv");

    private final String label;

    VideoFormat(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
