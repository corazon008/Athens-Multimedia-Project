package client;

import client.UI.FormatView;
import client.UI.ProtocolView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.concurrent.CountDownLatch;

public class StreamSettings {
    private static CountDownLatch latch;

    public static void Show() {
        latch = new CountDownLatch(1);

        new JFXPanel(); // Initialise JavaFX si pas déjà lancé

        Platform.runLater(() -> {
            Stage stage = new Stage(StageStyle.DECORATED);
            FormatView root = new FormatView(10, latch); // ⬅️ on passe le latch

            stage.setTitle("Choisissez vos paramètres");
            stage.setScene(new Scene(root, 400, 300));
            stage.initModality(Modality.APPLICATION_MODAL); // bloque jusqu'à fermeture
            stage.show();
        });
    }

    public static void Wait(){
        try {
            latch.await(); // Attend que l’utilisateur valide
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Show();
        Wait();
    }
}
