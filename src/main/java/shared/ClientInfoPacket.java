package shared;

import java.io.Serializable;

public class ClientInfoPacket implements Serializable {
    private static final long serialVersionUID = 1L;
    public double bandwidth;
    public VideoFormat videoFormat;

    public ClientInfoPacket(double bandwidth, VideoFormat videoFormat) {
        this.bandwidth = bandwidth;
        this.videoFormat = videoFormat;
    }
}
