package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import shared.ClientInfoPacket;
import shared.Enum.ProtocolType;
import shared.Enum.Resolution;
import shared.Enum.VideoFormat;
import shared.ServerInfo;
import shared.SharedInfo;

public class Server {
    private static Map<Integer, Resolution> bitrates = Map.of(400, Resolution.P240, 750, Resolution.P360, 1000, Resolution.P480, 2500, Resolution.P720, 4500, Resolution.P1080);

    public static void main(String[] args) throws Exception {
        FfmpegMakeAllResAndFormat();
        try (ServerSocket serverSocket = new ServerSocket(ServerInfo.getListenSocketPort())) {
            System.out.println("Serveur en attente d'un client...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connecté.");

            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            ClientInfoPacket clientInfoPacket = (ClientInfoPacket) ois.readObject();

            List<Resolution> availableResolutions = getResolutions(clientInfoPacket.downloadSpeed);

            System.out.println(availableResolutions);

            List<File> filteredFiles = filterVideo(clientInfoPacket.videoFormat, availableResolutions);
            System.out.println("Fichiers vidéo disponibles : " + filteredFiles);

            // Envoyer la liste des fichiers vidéo au client
            SendObject(filteredFiles, clientSocket);
        }
    }

    private static void SendObject(Object object, Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(object);
        oos.flush();
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

    private static List<Resolution> getResolutions(double downloadSpeed) {
        List<Resolution> availableResolutions = new ArrayList<>();
        for (Map.Entry<Integer, Resolution> entry : bitrates.entrySet()) {
            if (downloadSpeed >= entry.getKey()) {
                availableResolutions.add(entry.getValue());
            }
        }
        return availableResolutions;
    }

    private static File[] getVideoFiles() {
        return new File("videos").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
    }

    private static List<File> filterVideo(VideoFormat videoFormat, List<Resolution> l_resolutions) {
        File[] videoFiles = getVideoFiles();
        List<File> filteredFiles = new ArrayList<>();
        for (Resolution resolution : l_resolutions)
            for (File file : videoFiles)
                if (file.isFile() && file.getName().endsWith(videoFormat.toString().toLowerCase()) && file.getName().contains(resolution.getLabel()))
                    filteredFiles.add(file);

        return filteredFiles;
    }

    private static void FfmpegMakeAllResAndFormat() {
        List<Video> highestResVideos = new ArrayList<>();
        for (File file : getVideoFiles()) {
            Video video = new Video(file);
            boolean videoExists = false;
            for (Video t_video : highestResVideos) {
                if (video.SameVideo(t_video)) {
                    if (video.HaveHigherOrEqResolutionthan(t_video))
                        if (video.HaveBetterFormatthan(t_video)) {
                            highestResVideos.remove(t_video);
                            highestResVideos.add(video);
                        }
                    videoExists = true;
                    break;
                }
            }
            if (!videoExists) highestResVideos.add(video);
        }

        for (Video video : highestResVideos) {
            for (Resolution resolution : SharedInfo.getResolutions()) {
                for (VideoFormat format : SharedInfo.getVideoFormats()) {
                    Video newVideo = video.getVideoWithNew(format, resolution);
                    if (newVideo.HaveHigherResolutionthan(video))
                        continue;
                    File newfile = new File(newVideo.getVideoPath());
                    if (newfile.exists())
                        continue;
                    System.out.println(newfile.getPath());

                    FfmpegTranscode(video, newVideo);
                }
            }
        }
    }

    private static void FfmpegTranscode(Video input, Video output) {
        ProcessBuilder builder = new ProcessBuilder(
                ServerInfo.getFfmpegPath(),
                "-i", input.getVideoPath(),
                "-c:v", output.getCodec(),
                "-preset", "slow",
                "-crf", output.getBitrateVariation(),
                "-c:a", "aac",
                "-b:a", "128k",
                "-vf", String.format("scale=-2:%s", output.getIntResolution()),
                output.getVideoPath()
        );

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