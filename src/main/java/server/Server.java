package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import shared.*;
import shared.Enum.ProtocolType;
import shared.Enum.Resolution;
import shared.Enum.VideoFormat;

public class Server extends Connected {
    private static Map<Integer, Resolution> bitrates = Map.of(400, Resolution.P240, 750, Resolution.P360, 1000, Resolution.P480, 2500, Resolution.P720, 4500, Resolution.P1080);

    private static Process ffmpegProcess;

    public static void main(String[] args) throws Exception {
        //FfmpegHandler.FfmpegMakeAllResAndFormat();
        //filterVideo(VideoFormat.MP4, Resolution.P480);
        try (ServerSocket serverSocket = new ServerSocket(ServerInfo.GetListenSocketPort())) {
            System.out.println("Server started on port " + ServerInfo.GetListenSocketPort());
            System.out.println("Waiting for client connection...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            ClientInfoPacket clientInfoPacket = (ClientInfoPacket) ReadObject(clientSocket);

            Resolution highestResolution = getHighestResolutionsFromBandwidth(clientInfoPacket.downloadSpeed);
            System.out.println("Highest resolution supported by client connection : " + highestResolution);

            List<Video> filteredVideos = filterVideo(clientInfoPacket.videoFormat, highestResolution);
            System.out.println("Video filtered : " + filteredVideos);

            // Send video files to client
            SendObject(filteredVideos, clientSocket);

            ProtocolType protocol = (ProtocolType) ReadObject(clientSocket);
            System.out.println("Protocol selected by client : " + protocol);

            int videoIndex = (int) ReadObject(clientSocket);
            System.out.println("Video selected by client : " + filteredVideos.get(videoIndex));

            SendObject(FfmpegHandler.GetStreamURl(protocol), clientSocket);

            System.out.println("Waiting client to be ready...");
            while (true) {
                Object object = ReadObject(clientSocket);
                if (object instanceof String && object.equals("start")) {
                    System.out.println("Starting stream...");
                    FfmpegHandler.BeginStream(protocol, filteredVideos.get(videoIndex));
                    break;
                }
            }

            //Stop Server
            try {
                while (true) {
                    Object object = ReadObject(clientSocket);
                    if (object instanceof String && object.equals("stop")) {
                        System.out.println("Stopping stream...");
                        FfmpegHandler.StopStream();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static Resolution getHighestResolutionsFromBandwidth(double downloadSpeed) {
        Resolution highestRes = Resolution.P240;
        for (Map.Entry<Integer, Resolution> entry : bitrates.entrySet()) {
            if (downloadSpeed >= entry.getKey() && entry.getValue().getIntResolution() > highestRes.getIntResolution()) {
                highestRes = entry.getValue();
            }
        }
        return highestRes;
    }

    private static File[] getVideoFiles() {
        return new File("videos").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (!pathname.isFile()) {
                    return false;
                }
                for (VideoFormat extension : SharedInfo.getVideoFormats()) {
                    if (pathname.getName().toLowerCase().endsWith(extension.getLabel())) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private static List<Video> filterVideo(VideoFormat videoFormat, Resolution highestRes) {
        File[] videoFiles = getVideoFiles();
        List<Video> filteredVideos = new ArrayList<>();
        for (File file : videoFiles) {
            Video video = new Video(file);
            if (video.getVideoFormat() == videoFormat && video.getIntResolution() <= highestRes.getIntResolution())
                filteredVideos.add(video);
        }
        return filteredVideos;
    }
    
    
}