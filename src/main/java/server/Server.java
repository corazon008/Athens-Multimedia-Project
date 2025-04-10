package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import shared.*;
import shared.Enum.ProtocolType;
import shared.Enum.Resolution;
import shared.Enum.VideoFormat;

public class Server extends Connected {
    private static Map<Integer, Resolution> bitrates = Map.of(400, Resolution.P240, 750, Resolution.P360, 1000, Resolution.P480, 2500, Resolution.P720, 4500, Resolution.P1080);

    private static Process ffmpegProcess;

    public static void main(String[] args) throws Exception {
        //FfmpegMakeAllResAndFormat();
        //filterVideo(VideoFormat.MP4, Resolution.P480);
        try (ServerSocket serverSocket = new ServerSocket(ServerInfo.getListenSocketPort())) {
            System.out.println("Server started on port " + ServerInfo.getListenSocketPort());
            System.out.println("Waiting for client connection...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            ClientInfoPacket clientInfoPacket = (ClientInfoPacket) ReadObject(clientSocket);

            Resolution highestResolution = getHighestResolutions(clientInfoPacket.downloadSpeed);
            System.out.println("Highest resolution supported by client connection : " + highestResolution);

            List<Video> filteredVideos = filterVideo(clientInfoPacket.videoFormat, highestResolution);
            System.out.println("Video filtered : " + filteredVideos);

            // Send video files to client
            SendObject(filteredVideos, clientSocket);

            ProtocolType protocol = (ProtocolType) ReadObject(clientSocket);
            System.out.println("Protocol selected by client : " + protocol);

            int videoIndex = (int) ReadObject(clientSocket);
            System.out.println("Video selected by client : " + filteredVideos.get(videoIndex));

            SendObject(getStreamURl(protocol), clientSocket);

            System.out.println("Waiting client to be ready...");
            while (true) {
                Object object = ReadObject(clientSocket);
                if (object instanceof String && object.equals("start")) {
                    System.out.println("Starting stream...");
                    BeginStream(protocol, filteredVideos.get(videoIndex));
                    break;
                }
            }

            //Stop Server
            try {
                while (true) {
                    Object object = ReadObject(clientSocket);
                    if (object instanceof String && object.equals("stop")) {
                        System.out.println("Stopping stream...");
                        stopStream();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static void BeginStream(ProtocolType protocol, Video video) {
        ProcessBuilder builder = new ProcessBuilder(ServerInfo.getFfmpegPath(), "-re", // Real-time streaming
                "-i", video.getVideoPath(), "-f", "mpegts", getStreamURl(protocol));

        builder.inheritIO();
        try {
            ffmpegProcess = builder.start();
            //ffmpegProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopStream() {
        if (ffmpegProcess != null && ffmpegProcess.isAlive()) {
            System.out.println("Stopping ffmpeg process...");
            ffmpegProcess.destroy(); // envoie SIGTERM

            try {
                // Attend un peu pour voir s’il s’arrête gentiment
                if (!ffmpegProcess.waitFor(2, TimeUnit.SECONDS)) {
                    System.out.println("Forcing ffmpeg to stop...");
                    ffmpegProcess.destroyForcibly(); // envoie SIGKILL
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static String getStreamURl(ProtocolType protocol) {
        switch (protocol) {
            case UDP:
                return String.format("udp://%s:%d", ServerInfo.getFfmpegListenSocketIP(), ServerInfo.getFfmpegPort());
            case TCP:
                return String.format("tcp://%s:%d", ServerInfo.getFfmpegListenSocketIP(), ServerInfo.getFfmpegPort());
            case RTP:
                return String.format("rtp://%s:%d", ServerInfo.getFfmpegListenSocketIP(), ServerInfo.getFfmpegPort());
            default:
                return String.format("udp://%s:%d", ServerInfo.getFfmpegListenSocketIP(), ServerInfo.getFfmpegPort());
        }
    }

    private static Resolution getHighestResolutions(double downloadSpeed) {
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

    private static void FfmpegMakeAllResAndFormat() {
        List<Video> highestResVideos = new ArrayList<>();
        for (File file : getVideoFiles()) {
            Video video = new Video(file);
            boolean videoExists = false;
            for (Video t_video : highestResVideos) {
                if (video.SameVideo(t_video)) {
                    if (video.HaveHigherOrEqResolutionthan(t_video)) if (video.HaveBetterFormatthan(t_video)) {
                        highestResVideos.remove(t_video);
                        highestResVideos.add(video);
                    }
                    videoExists = true;
                    break;
                }
            }
            if (!videoExists) highestResVideos.add(video);
        }

        ExecutorService executor = Executors.newFixedThreadPool(4); // max 4 transcodages en parallèle

        for (Video video : highestResVideos) {
            for (Resolution resolution : SharedInfo.getResolutions()) {
                for (VideoFormat format : SharedInfo.getVideoFormats()) {
                    Video newVideo = video.getVideoWithNew(format, resolution);
                    if (newVideo.HaveHigherResolutionthan(video)) continue;
                    File newfile = new File(newVideo.getVideoPath());
                    if (newfile.exists()) continue;
                    System.out.println(newfile.getPath());

                    // Submit task to thread pool
                    executor.submit(() -> FfmpegTranscode(new Video(video), newVideo));
                }
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void FfmpegTranscode(Video input, Video output) {
        ProcessBuilder builder = new ProcessBuilder(ServerInfo.getFfmpegPath(), "-i", input.getVideoPath(), "-c:v", output.getCodec(), "-preset", "slow", "-crf", output.getBitrateVariation(), "-c:a", "aac", "-b:a", "128k", "-vf", String.format("scale=-2:%s", output.getIntResolution()), output.getVideoPath());

        System.out.println(builder.command());

        builder.inheritIO();
        try {
            Process ffmpegProcess = builder.start();
            ffmpegProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}