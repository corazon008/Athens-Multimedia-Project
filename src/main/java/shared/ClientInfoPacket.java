package shared;

import shared.Enum.VideoFormat;

import java.io.Serializable;

public class ClientInfoPacket implements Serializable {
    private static final long serialVersionUID = 1L;
    public double downloadSpeed;
    public VideoFormat videoFormat;

    public ClientInfoPacket(double downloadSpeed, VideoFormat videoFormat) {
        this.downloadSpeed = downloadSpeed;
        this.videoFormat = videoFormat;
    }
}
