package client.UI;

import client.UI.Scene.Stream;
import client.UI.Scene.StreamSettings;
import client.UI.Views.BaseView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class FXManager extends Application {
    private static Logger logger = Logger.getLogger("FXManager");

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

    public static void StartStreamView(String url, Runnable onStart, Runnable onClose) {
        // Wait for the JavaFX application to start
        try {
            startupLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        currentViewLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            logger.info("Starting Stream View...");
            Stream stream = new Stream(url, onStart);
            primaryStage.setScene(stream);
            primaryStage.show();
            primaryStage.setTitle("Streaming");
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("Closing stream");
                onClose.run();
                primaryStage.close();
                stream.Stop();
                currentViewLatch.countDown();
            });
        });
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

    public static void WaitCurrentView() {
        try {
            currentViewLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
