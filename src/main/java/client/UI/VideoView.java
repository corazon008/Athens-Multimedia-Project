package client.UI;

import client.UserSelection;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleGroup;

public class VideoView extends BaseView {
    public VideoView(double spacing, Runnable onValidate) {
        super(spacing, onValidate);

        ToggleGroup group = new ToggleGroup();

        for (int i = 0; i < UserSelection.videosAvailable.size(); i++) {
            RadioButton videoButton = new RadioButton(UserSelection.videosAvailable.get(i).getVideoFileName());
            videoButton.setToggleGroup(group);
            videoButton.setUserData(i);
            this.getChildren().addAll(videoButton);

        }

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            UserSelection.selectedVideoIndex = (int) group.getSelectedToggle().getUserData();
            onValidate.run();
        });

        this.getChildren().addAll(new Separator(), submitButton);
    }
}