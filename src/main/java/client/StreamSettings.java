package client;

import client.UI.BaseView;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.concurrent.CountDownLatch;

public class StreamSettings {
    private static Stage stage;
    private static JFXPanel jfxPanel;
    private CountDownLatch latch;

    public void Show(Class<? extends BaseView> viewClass) {
        latch = new CountDownLatch(1);
        if (jfxPanel == null) {
            jfxPanel = new JFXPanel(); // Initialize the JavaFX environment
        }

        Platform.runLater(() -> {
            try {
                if (stage == null) {
                    stage = new Stage(StageStyle.DECORATED);
                    stage.initModality(Modality.APPLICATION_MODAL);
                }

                BaseView root = viewClass.getConstructor(double.class, Runnable.class).newInstance(10.0, (Runnable) () -> {
                    //stage.close();
                    latch.countDown();
                });

                stage.setTitle("Choose your settings");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void Wait() {
        try {
            latch.await(); // Attend que l’utilisateur valide
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void Close() {
        if (stage != null) {
            Platform.runLater(() -> {
                stage.close();
                stage = null;
            });
        }

        if (jfxPanel != null) {
            Platform.runLater(() -> {
                jfxPanel = null;
            });
        }
    }
}
