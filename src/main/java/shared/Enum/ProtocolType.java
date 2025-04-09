package shared.Enum;

import java.io.Serializable;

public enum ProtocolType implements Serializable {
    UDP("udp"),
    TCP("tcp"),
    RTP("rtp");

    private final String label;

    ProtocolType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
