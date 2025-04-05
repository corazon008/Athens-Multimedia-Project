package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import shared.ProtocolType;
import shared.ServerInfo;

public class Server {
    public static void main(String[] args) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(ServerInfo.getListenSocketPort())) {
            System.out.println("Serveur en attente d'un client...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connecté.");

            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            ProtocolType protocol = (ProtocolType) ois.readObject();

            String streamUrl = getStreamURl(protocol);

            System.out.println("Streaming via : " + streamUrl);

            // Lancer FFmpeg pour streamer la vidéo
            ProcessBuilder builder = new ProcessBuilder(
                    ServerInfo.getFfmpegPath(),
                    "-re", "-i", "videos/Outside-1080p.mp4",
                    "-f", "mpegts", streamUrl
            );
            builder.inheritIO();
            Process ffmpegProcess = builder.start();

            // Envoyer au client l'URL du stream
            OutputStream out = clientSocket.getOutputStream();
            out.write((streamUrl + "\n").getBytes());
            out.flush();

            ffmpegProcess.waitFor();
        }
    }

    private static String getStreamURl(ProtocolType protocol) {
        switch (protocol){
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
}