package client.UI;

import client.UI.Scene.StreamSettings;
import client.UI.Views.BaseView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * FXManager is a class that manages the JavaFX application lifecycle and UI.
 */
public class FXManager extends Application {
    private static Logger logger = Logger.getLogger("FXManager");
    private static String ffplayPath = "ffmpeg-win\\bin\\ffplay.exe";

    private static Stage primaryStage;
    private static CountDownLatch startupLatch;
    private static CountDownLatch currentViewLatch;

    public static void StartApp() {
        startupLatch = new CountDownLatch(1);
        new Thread(()->{
            logger.info("Starting Application UI...");
            launch();
        }).start();
    }

    public static void SetStreamSettingsView(Class<? extends BaseView> viewClass) {
        try {
            startupLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        currentViewLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            primaryStage.setScene(new StreamSettings(viewClass, currentViewLatch));
            primaryStage.show();
            primaryStage.setTitle("Stream Settings");
        });
    }

    public static void StartStreamViewFfplay(String url, Runnable onStart, Runnable onClose) {
        currentViewLatch = new CountDownLatch(1);

        ProcessBuilder builder = new ProcessBuilder(ffplayPath, url, "-y", "720");

        System.out.println(builder.command());

        builder.inheritIO();
        try {
            onStart.run();
            Process ffmpegProcess = builder.start();
            ffmpegProcess.waitFor();
            onClose.run();
            currentViewLatch.countDown();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXManager.primaryStage = primaryStage;
        primaryStage.setTitle("Video Player");
        primaryStage.show();
        startupLatch.countDown();
    }

    public static void Close() {
        Platform.exit();
    }

    public static void WaitCurrentView() {
        try {
            currentViewLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
