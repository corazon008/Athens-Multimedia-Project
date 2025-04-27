package client.UI.Scene;

import client.UI.Views.BaseView;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.util.concurrent.CountDownLatch;

/**
 * Class that inherit from Scene and display the stream settings view
 */
public class StreamSettings extends Scene {
    private CountDownLatch latch;

    public StreamSettings(Class<? extends BaseView> viewClass, CountDownLatch latch) {
        super(new VBox());
        this.latch = latch;
        InitUI(viewClass);
    }

    private void InitUI(Class<? extends BaseView> viewClass) {
        try {
            BaseView root = viewClass.getConstructor(double.class, Runnable.class)
                    .newInstance(10.0, (Runnable) () -> latch.countDown());
            super.setRoot(root);

            root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                        if (newWindow != null) {
                            newWindow.sizeToScene();
                        }
                    });
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}