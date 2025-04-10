package client.UI.Scene;

import client.UI.Views.BaseView;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.util.concurrent.CountDownLatch;

public class StreamSettings extends Scene {
    private CountDownLatch latch;

    public StreamSettings(Class<? extends BaseView> viewClass, CountDownLatch latch) {
        super(new VBox());
        this.latch = latch;
        InitUI(viewClass);
    }

    private void InitUI(Class<? extends BaseView> viewClass) {
        try {

            BaseView root = viewClass.getConstructor(double.class, Runnable.class).newInstance(10.0, (Runnable) () -> {
                //stage.close();
                latch.countDown();
            });

            super.setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}