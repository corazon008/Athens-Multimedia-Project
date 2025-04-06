package client.UI;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import shared.ProtocolType;

public class ProtocolView extends VBox {
    public ProtocolView(double spacing) {
        super(spacing);

        // Crée un groupe de boutons radio
        ToggleGroup group = new ToggleGroup();

        RadioButton udpButton = new RadioButton("UDP");
        udpButton.setToggleGroup(group);
        udpButton.setSelected(true);
        udpButton.setUserData(ProtocolType.UDP);

        RadioButton tcpButton = new RadioButton("TCP");
        tcpButton.setToggleGroup(group);

        RadioButton rtpButton = new RadioButton("RTP");
        rtpButton.setToggleGroup(group);

        Label selectedLabel = new Label("No protocol selected");

        // Gérer le changement de sélection
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                RadioButton selected = (RadioButton) group.getSelectedToggle();
                selectedLabel.setText("Protocol : " + selected.getText());
            }
        });

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            group.getSelectedToggle();
        });

        this.getChildren().addAll(udpButton, tcpButton, rtpButton, selectedLabel, new Separator(), submitButton);
    }
}
