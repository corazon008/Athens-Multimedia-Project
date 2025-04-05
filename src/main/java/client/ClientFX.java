package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.bytedeco.javacv.*;

import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;

public class ClientFX extends Application {

    private static String streamUrl;
    private ImageView imageView;

    public static void startApp(String url) {
        streamUrl = url;
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        imageView = new ImageView();
        StackPane root = new StackPane(imageView);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Streaming Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(this::startStreaming).start();
    }

    private void startStreaming() {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(streamUrl);
            grabber.start();

            Java2DFrameConverter converter = new Java2DFrameConverter();

            while (true) {
                Frame frame = grabber.grabImage();
                if (frame == null) break;

                BufferedImage bufferedImage = converter.getBufferedImage(frame);
                if (bufferedImage != null) {
                    Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                    javafx.application.Platform.runLater(() -> imageView.setImage(fxImage));
                }
            }

            grabber.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
