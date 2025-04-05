package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StreamSettings extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Crée un groupe de boutons radio
        ToggleGroup group = new ToggleGroup();

        RadioButton udpButton = new RadioButton("UDP");
        udpButton.setToggleGroup(group);

        RadioButton tcpButton = new RadioButton("TCP");
        tcpButton.setToggleGroup(group);

        RadioButton rtpButton = new RadioButton("RTP");
        rtpButton.setToggleGroup(group);

        Label selectedLabel = new Label("Aucune option sélectionnée");

        // Gérer le changement de sélection
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                selectedLabel.setText("Sélectionné : " + selected.getText());
            }
        });

        VBox layout = new VBox(10); // espace de 10px entre les éléments
        layout.getChildren().addAll(udpButton, tcpButton, rtpButton, selectedLabel);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setTitle("Exemple RadioButton JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
