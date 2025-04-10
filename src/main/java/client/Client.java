package client;

import client.UI.FXManager;
import client.UI.Views.FormatView;
import client.UI.Views.ProtocolView;
import client.UI.Views.VideoView;
import shared.Video;
import shared.ClientInfoPacket;
import shared.Connected;
import shared.ServerInfo;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class Client extends Connected {
    public static void main(String[] args) throws InterruptedException {

        FXManager.StartApp();
        FXManager.SetStreamSettingsView(FormatView.class);

        SpeedTest speedTest = new SpeedTest(5000);
        speedTest.StartSpeedTest();


        speedTest.WaitForSpeedTest();
        double downloadSpeed = speedTest.getDownloadSpeed().doubleValue() / 1_000; // Convert in Kbps
        System.out.println("Vitesse de téléchargement : " + downloadSpeed + " Kbps");

        FXManager.WaitCurrentView();
        System.out.println("Format sélectionné : " + UserSelection.format);
        try (Socket socket = new Socket(ServerInfo.getListenSocketIP(), ServerInfo.getListenSocketPort())) {

            ClientInfoPacket clientInfo = new ClientInfoPacket(downloadSpeed, UserSelection.format);
            SendObject(clientInfo, socket);

            UserSelection.videosAvailable = (List<Video>) ReadObject(socket);
            System.out.println("Videos available for you : " + UserSelection.videosAvailable);

            FXManager.SetStreamSettingsView(ProtocolView.class);
            FXManager.WaitCurrentView();
            SendObject(UserSelection.protocol, socket);

            FXManager.SetStreamSettingsView(VideoView.class);
            FXManager.WaitCurrentView();
            SendObject(UserSelection.selectedVideoIndex, socket);

            String streamUrl = (String) ReadObject(socket);
            System.out.println("Stream URL : " + streamUrl);

            Runnable stopServer = () -> {
                try {
                    if (!socket.isClosed()) {
                        SendObject("stop", socket);
                        System.out.println("Sending stop signal to server...");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            Runnable startServer = () -> {
                try {
                    if (!socket.isClosed()) {
                        SendObject("start", socket);
                        System.out.println("Sending start signal to server...");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            FXManager.StartStreamView(streamUrl, startServer, stopServer);

            FXManager.WaitCurrentView();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}