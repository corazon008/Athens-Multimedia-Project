package shared;

public class ServerInfo {
    private static String listenSocketIP = "localhost";
    private static int listenSocketPort = 5000;
    private static String ffmpegPath = "ffmpeg-win\\bin\\ffmpeg.exe";
    private static String ffmpegListenSocketIP = "localhost";
    private static int ffmpegPort = 5001;

    public static String getListenSocketIP() {
        return listenSocketIP;
    }

    public static int getListenSocketPort() {
        return listenSocketPort;
    }

    public static String getFfmpegPath() {
        return ffmpegPath;
    }

    public static String getFfmpegListenSocketIP() {
        return ffmpegListenSocketIP;
    }

    public static int getFfmpegPort() {
        return ffmpegPort;
    }
}
