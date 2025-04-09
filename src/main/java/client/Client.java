package client;

import client.UI.Views.FormatView;
import client.UI.Views.ProtocolView;
import client.UI.StreamSettings;
import client.UI.Views.VideoView;
import shared.Video;
import shared.ClientInfoPacket;
import shared.Connected;
import shared.ServerInfo;

import java.net.*;
import java.util.List;

public class Client extends Connected {
    public static void main(String[] args) throws InterruptedException {
        SpeedTest speedTest = new SpeedTest(5000);
        speedTest.StartSpeedTest();

        StreamSettings formatView = new StreamSettings();
        formatView.Show(FormatView.class);

        speedTest.WaitForSpeedTest();
        double downloadSpeed = speedTest.getDownloadSpeed().doubleValue() / 1_000; // Convert in Kbps
        System.out.println("Vitesse de téléchargement : " + downloadSpeed + " Kbps");

        formatView.Wait();
        System.out.println("Format sélectionné : " + UserSelection.format);
        try (Socket socket = new Socket(ServerInfo.getListenSocketIP(), ServerInfo.getListenSocketPort())) {

            ClientInfoPacket clientInfo = new ClientInfoPacket(downloadSpeed, UserSelection.format);
            SendObject(clientInfo, socket);

            UserSelection.videosAvailable = (List<Video>) ReadObject(socket);
            System.out.println("Videos available for you : " + UserSelection.videosAvailable);

            StreamSettings protocolView = new StreamSettings();
            protocolView.Show(ProtocolView.class);
            protocolView.Wait();

            StreamSettings videoView = new StreamSettings();
            videoView.Show(VideoView.class);
            videoView.Wait();


            StreamSettings.Close();

            SendObject(UserSelection.selectedVideoIndex, socket);

        } catch (Exception e) {
            e.printStackTrace();
            StreamSettings.Close();
        }
    }
}

