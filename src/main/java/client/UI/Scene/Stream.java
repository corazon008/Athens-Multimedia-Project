package client.UI.Scene;

import client.UserSelection;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import shared.Enum.ProtocolType;

import java.awt.image.BufferedImage;

public class Stream extends Scene {
    private final String streamUrl;
    private final Runnable onStartCallback;
    private ImageView imageView;
    private FFmpegFrameGrabber grabber;

    public Stream(String url, Runnable onStart) {
        super(new StackPane(), 1280, 720);
        this.streamUrl = url;
        this.onStartCallback = onStart;
        InitUI();
    }

    private void InitUI() {
        imageView = new ImageView();
        imageView.fitHeightProperty().bind(super.heightProperty());
        imageView.fitWidthProperty().bind(super.widthProperty());
        imageView.setPreserveRatio(true);
        StackPane root = new StackPane(imageView);
        super.setRoot(root);

        onStartCallback.run();
        new Thread(this::StartFfmpegStreaming).start();
        System.out.println("Streaming launched.");
    }

    private void StartFfmpegStreaming() {
        try {
            FFmpegLogCallback.set();
            grabber = new FFmpegFrameGrabber(streamUrl);
            if (UserSelection.protocol == ProtocolType.RTP) {
                grabber.setFormat("sdp");
                grabber.setOption("protocol_whitelist", "file,udp,rtp");
                grabber.setOption("analyzeduration", "15000000"); // 15s
                grabber.setOption("probesize", "15000000");
            }
            grabber.start();
            System.out.println("Started streaming from: " + streamUrl);

            Java2DFrameConverter converter = new Java2DFrameConverter();

            Frame frame;
            while ((frame = grabber.grabImage()) != null) {
                BufferedImage bufferedImage = converter.getBufferedImage(frame);
                if (bufferedImage != null) {
                    Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                    Platform.runLater(() -> imageView.setImage(fxImage));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Stop();
        }
    }

    public void Stop() {
        if (grabber != null) {
            try {
                System.out.println("Stopping streaming...");
                grabber.close();
                System.out.println("Stopped streaming.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
