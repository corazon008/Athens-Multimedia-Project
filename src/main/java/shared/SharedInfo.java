package shared;

import server.Server;
import shared.Enum.EndpointType;
import shared.Enum.ProtocolType;
import shared.Enum.Resolution;
import shared.Enum.VideoFormat;

import java.util.List;

/**
 * SharedInfo class provides shared information and utility methods for the application.
 * It contains lists of supported resolutions, video formats, and protocol types.
 * It also provides a method to generate stream URLs based on the selected protocol and endpoint type.
 */
public class SharedInfo {
    private static List<Resolution> resolutions = List.of(Resolution.P240, Resolution.P360, Resolution.P480, Resolution.P720, Resolution.P1080);

    private static List<VideoFormat> videoFormats = List.of(VideoFormat.AVI, VideoFormat.MP4, VideoFormat.MKV);

    private static List<ProtocolType> protocolTypes = List.of(ProtocolType.TCP, ProtocolType.UDP, ProtocolType.RTP);

    public static List<Resolution> getResolutions() {
        return resolutions;
    }

    public static List<VideoFormat> getVideoFormats() {
        return videoFormats;
    }

    public static List<ProtocolType> getProtocolTypes() {
        return protocolTypes;
    }

    public static String GetStreamUrl(ProtocolType protocol, EndpointType endpointType) {
        if (protocol == null) {
            throw new IllegalArgumentException("Protocol cannot be null");
        }
        if (endpointType == null) {
            throw new IllegalArgumentException("EndpointType cannot be null");
        }

        if (endpointType == EndpointType.CLIENT) {
            switch (protocol) {
                case UDP:
                    return String.format("udp://%s:%d", "0.0.0.0", ServerInfo.serverFfmpegVideoPort);
                case TCP:
                    return String.format("tcp://%s:%d", ServerInfo.serverIP, ServerInfo.serverFfmpegVideoPort);
                case RTP:
                    return String.format("rtp://%s:%d", "0.0.0.0", ServerInfo.serverFfmpegVideoPort);
                default:
                    throw new IllegalArgumentException("Unknown protocol: " + protocol);
            }
        } else {
            switch (protocol) {
                case UDP:
                    return String.format("udp://%s:%d", Server.clientIP, ServerInfo.serverFfmpegVideoPort);
                case TCP:
                    return String.format("tcp://%s:%d/?listen", "0.0.0.0", ServerInfo.serverFfmpegVideoPort);
                case RTP:
                    return String.format("rtp://%s:%d", Server.clientIP, ServerInfo.serverFfmpegVideoPort);
                default:
                    throw new IllegalArgumentException("Unknown protocol: " + protocol);
            }
        }
    }
}
