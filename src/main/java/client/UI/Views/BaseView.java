package client.UI.Views;

import javafx.scene.layout.VBox;

public abstract class BaseView extends VBox {
    public BaseView(double spacing, Runnable onValidate) {
        super(spacing);
    }
}
