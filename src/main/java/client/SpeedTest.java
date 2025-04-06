package client;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

import java.math.BigDecimal;

public class SpeedTest {
    private int duration;
    private BigDecimal downloadSpeed;
    private boolean finished = false;
    private String server1 = "http://mirror.internode.on.net/pub/speed/SpeedTest_16MB";
    private String server2 = "http://speedtest.tele2.net/100MB.zip";
    private String server3 = "http://speedtest.serverius.net/files/10mb.bin";
    private String server4 = "http://speedtest.ftp.otenet.gr/files/test1Mb.db";

    // duration in milliseconds
    public SpeedTest(int duration) {
        this.duration = duration;
    }

    public void StartSpeedTest() throws InterruptedException {
        testDownloadSpeed(server2);
    }

    public void WaitForSpeedTest() throws InterruptedException {
        while (!finished)
            Thread.sleep(500);
    }

    public void StartSpeedTestAllServer() throws InterruptedException {
        for (int i = 0; i < 4; i++) {
            System.out.println("Test de vitesse de téléchargement sur le serveur " + (i + 1) + "...");
            switch (i) {
                case 0:
                    testDownloadSpeed(server1);
                    break;
                case 1:
                    testDownloadSpeed(server2);
                    break;
                case 2:
                    testDownloadSpeed(server3);
                    break;
                case 3:
                    testDownloadSpeed(server4);
                    break;
            }
        }
    }

    private void testDownloadSpeed(String server) throws InterruptedException {
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        speedTestSocket.startFixedDownload(server, duration);

        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {
            @Override
            public void onCompletion(SpeedTestReport report) {
                System.out.println("[COMPLETED] rate in Moctet/s : " + report.getTransferRateOctet().doubleValue() / 1000000);
                System.out.println("[COMPLETED] rate in Mbit/s   : " + report.getTransferRateBit().doubleValue() / 1000000);
                downloadSpeed = report.getTransferRateBit();
                finished = true;
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                System.err.println("[ERROR] " + errorMessage);
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
//                System.out.println("[PROGRESS] progress : " + percent + "%");
//                System.out.println("[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
//                System.out.println("[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
            }
        });
    }

    public BigDecimal getDownloadSpeed() {
        return downloadSpeed;
    }
}
