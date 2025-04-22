package client.UI.Views;

import javafx.scene.control.*;
import client.UserSelection;
import shared.Enum.VideoFormat;

/**
 * FormatView is a class that extends BaseView and represents a view for selecting the video format.
 */
public class FormatView extends BaseView {
    public FormatView(double spacing, Runnable onValidate) {
        super(spacing, onValidate);

        ToggleGroup group = new ToggleGroup();

        RadioButton aviButton = new RadioButton("AVI");
        aviButton.setToggleGroup(group);
        aviButton.setUserData(VideoFormat.AVI);

        RadioButton mp4Button = new RadioButton("MP4");
        mp4Button.setToggleGroup(group);
        mp4Button.setSelected(true);
        mp4Button.setUserData(VideoFormat.MP4);

        RadioButton mkvButton = new RadioButton("MKV");
        mkvButton.setToggleGroup(group);
        mkvButton.setUserData(VideoFormat.MKV);

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            UserSelection.format = (VideoFormat) group.getSelectedToggle().getUserData();
            onValidate.run();
        });

        this.getChildren().addAll(aviButton, mp4Button, mkvButton, new Separator(), submitButton);
    }
}
