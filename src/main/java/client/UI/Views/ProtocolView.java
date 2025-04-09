package client.UI.Views;

import javafx.scene.control.*;
import shared.Enum.ProtocolType;
import client.UserSelection;

public class ProtocolView extends BaseView {
    public ProtocolView(double spacing, Runnable onValidate) {
        super(spacing, onValidate);

        ToggleGroup group = new ToggleGroup();

        RadioButton udpButton = new RadioButton("UDP");
        udpButton.setToggleGroup(group);
        udpButton.setSelected(true);
        udpButton.setUserData(ProtocolType.UDP);

        RadioButton tcpButton = new RadioButton("TCP");
        tcpButton.setToggleGroup(group);
        tcpButton.setUserData(ProtocolType.TCP);

        RadioButton rtpButton = new RadioButton("RTP");
        rtpButton.setToggleGroup(group);
        rtpButton.setUserData(ProtocolType.RTP);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            UserSelection.protocol = (ProtocolType) group.getSelectedToggle().getUserData();
            onValidate.run();
        });

        this.getChildren().addAll(udpButton, tcpButton, rtpButton, new Separator(), submitButton);
    }
}
