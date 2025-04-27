package client.UI.Views;

import javafx.scene.control.*;
import shared.Enum.ProtocolType;
import client.UserSelection;
import shared.Enum.Resolution;

/**
 * ProtocolView is a class that extends BaseView and represents a view for selecting the protocol.
 */
public class ProtocolView extends BaseView {
    public ProtocolView(double spacing, Runnable onValidate) {
        super(spacing, onValidate);

        ToggleGroup group = new ToggleGroup();

        RadioButton udpButton = new RadioButton("UDP");
        udpButton.setToggleGroup(group);
        //udpButton.setSelected(true);
        udpButton.setUserData(ProtocolType.UDP);

        RadioButton tcpButton = new RadioButton("TCP");
        tcpButton.setToggleGroup(group);
        tcpButton.setUserData(ProtocolType.TCP);

        RadioButton rtpButton = new RadioButton("RTP");
        rtpButton.setToggleGroup(group);
        rtpButton.setUserData(ProtocolType.RTP);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            if (group.getSelectedToggle() == null) {
                UserSelection.protocol = ChooseProtocolAuto(UserSelection.videosAvailable.get(UserSelection.selectedVideoIndex).getResolution());
            }else {
                UserSelection.protocol = (ProtocolType) group.getSelectedToggle().getUserData();
            }
            onValidate.run();
        });

        this.getChildren().addAll(udpButton, tcpButton, rtpButton, new Separator(), submitButton);
    }

    private static ProtocolType ChooseProtocolAuto(Resolution resolution) {
        if (resolution == Resolution.P240)
            return ProtocolType.TCP;
        else if (resolution == Resolution.P360 || resolution == Resolution.P480)
            return ProtocolType.UDP;
        else if (resolution == Resolution.P720 || resolution == Resolution.P1080)
            return ProtocolType.RTP;
        else
            return ProtocolType.TCP;
    }
}
