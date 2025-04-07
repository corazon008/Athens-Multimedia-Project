package client.UI;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import shared.UserSelection;
import shared.Enum.VideoFormat;

import java.util.concurrent.CountDownLatch;

public class FormatView extends VBox {
    public FormatView(double spacing, CountDownLatch latch) {
        super(spacing);

        // Crée un groupe de boutons radio
        ToggleGroup group = new ToggleGroup();

        RadioButton aviButton = new RadioButton("AVI");
        aviButton.setToggleGroup(group);
        aviButton.setSelected(true);
        aviButton.setUserData(VideoFormat.AVI);

        RadioButton mp4Button = new RadioButton("MP4");
        mp4Button.setToggleGroup(group);
        mp4Button.setUserData(VideoFormat.MP4);

        RadioButton mkvButton = new RadioButton("MKV");
        mkvButton.setToggleGroup(group);
        mkvButton.setUserData(VideoFormat.MKV);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            UserSelection.format = (VideoFormat) group.getSelectedToggle().getUserData();
            latch.countDown();
        });

        this.getChildren().addAll(aviButton, mp4Button, mkvButton, new Separator(), submitButton);
    }
}
