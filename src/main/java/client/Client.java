package client;

import client.UI.FXManager;
import client.UI.Views.FormatView;
import client.UI.Views.ProtocolView;
import client.UI.Views.ChooseVideoView;
import shared.*;
import shared.Enum.EndpointType;
import shared.Enum.ProtocolType;
import shared.Enum.Resolution;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.logging.Logger;

public class Client extends Connected {
    private static Logger logger = Logger.getLogger("Client");

    public static void main(String[] args) throws InterruptedException {
        FXManager.StartApp();
        FXManager.SetStreamSettingsView(FormatView.class);

        SpeedTest speedTest = new SpeedTest(5000);
        logger.info("Starting speed test...");
        speedTest.StartSpeedTest();
        speedTest.WaitForSpeedTest();

        double downloadSpeed = speedTest.getDownloadSpeed().doubleValue() / 1_000; // Convert in Kbps
        System.out.println("Vitesse de téléchargement : " + downloadSpeed + " Kbps");

        FXManager.WaitCurrentView();
        System.out.println("Format sélectionné : " + UserSelection.format);

        try (Socket socket = new Socket(ServerInfo.serverIP, ServerInfo.serverSocketPort)) {
            ClientInfoPacket clientInfo = new ClientInfoPacket(downloadSpeed, UserSelection.format);
            SendObject(clientInfo, socket);

            UserSelection.videosAvailable = (List<Video>) ReadObject(socket);
            System.out.println("Videos available for you : " + UserSelection.videosAvailable);

            FXManager.SetStreamSettingsView(ChooseVideoView.class);
            FXManager.WaitCurrentView();
            SendObject(UserSelection.selectedVideoIndex, socket);

            FXManager.SetStreamSettingsView(ProtocolView.class);
            FXManager.WaitCurrentView();
            SendObject(UserSelection.protocol, socket);

            String streamUrl = SharedInfo.GetStreamUrl(UserSelection.protocol, EndpointType.CLIENT);
            System.out.println("Stream URL : " + streamUrl);

            Runnable startServer = () -> {
                try {
                    if (!socket.isClosed()) {
                        SendObject("start", socket);
                        logger.info("Sending start signal to server...");
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            Runnable stopServer = () -> {
                try {
                    if (!socket.isClosed()) {
                        SendObject("stop", socket);
                        logger.info("Sending stop signal to server...");
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

    public static ProtocolType ChooseProtocolAuto(Resolution resolution) {
        if (resolution == Resolution.P240)
            return ProtocolType.TCP;
        else if (resolution == Resolution.P480 || resolution == Resolution.P720)
            return ProtocolType.UDP;
        else if (resolution == Resolution.P1080)
            return ProtocolType.RTP;
        else
            return ProtocolType.TCP;
    }
}