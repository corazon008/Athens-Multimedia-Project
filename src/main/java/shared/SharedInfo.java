package shared;

import shared.Enum.ProtocolType;
import shared.Enum.Resolution;
import shared.Enum.VideoFormat;

import java.util.List;

public class SharedInfo {
    private static List<Resolution> resolutions = List.of(
            Resolution.P240,
            Resolution.P360,
            Resolution.P480,
            Resolution.P720,
            Resolution.P1080
    );

    private static List<VideoFormat> videoFormats = List.of(
            VideoFormat.AVI,
            VideoFormat.MP4,
            VideoFormat.MKV
    );

    private static List<ProtocolType> protocolTypes = List.of(
            ProtocolType.TCP,
            ProtocolType.UDP,
            ProtocolType.RTP
    );

    public static List<Resolution> getResolutions() {
        return resolutions;
    }

    public static List<VideoFormat> getVideoFormats() {
        return videoFormats;
    }

    public static List<ProtocolType> getProtocolTypes() {
        return protocolTypes;
    }
}
