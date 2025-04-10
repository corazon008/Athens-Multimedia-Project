package shared;

public class ServerInfo {
    private static String listenSocketIP = "localhost";
    private static int listenSocketPort = 5000;
    private static String ffmpegListenSocketIP = "localhost";
    private static int ffmpegPort = 5001;

    public static String GetListenSocketIP() {
        return listenSocketIP;
    }

    public static int GetListenSocketPort() {
        return listenSocketPort;
    }

    public static String GetFfmpegListenSocketIP() {
        return ffmpegListenSocketIP;
    }

    public static int GetFfmpegPort() {
        return ffmpegPort;
    }
}
