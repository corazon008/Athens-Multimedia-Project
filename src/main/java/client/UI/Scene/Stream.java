package client.UI.Scene;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;

public class Stream extends Scene {
    private final String streamUrl;
    private final Runnable onStartCallback;
    private ImageView imageView;
    private FFmpegFrameGrabber grabber;

    public Stream(String url, Runnable onStart) {
        super(new StackPane(), 800, 600);
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
            grabber = new FFmpegFrameGrabber(streamUrl);
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
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void Stop() {
        if (grabber != null) {
            try {
                grabber.stop();
                grabber.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
