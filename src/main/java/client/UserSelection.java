package client;

import shared.Video;
import shared.Enum.ProtocolType;
import shared.Enum.VideoFormat;

import java.util.List;

public class UserSelection {
    public static VideoFormat format;
    public static ProtocolType protocol;
    public static List<Video> videosAvailable;
    public static int selectedVideoIndex;
}
