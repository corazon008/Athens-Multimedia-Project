package client.UI.Views;

import javafx.scene.layout.VBox;

/**
 * Base class for all views in the application.
 * This class extends VBox and provides a constructor that takes a spacing value and a validation callback.
 */
public abstract class BaseView extends VBox {
    public BaseView(double spacing, Runnable onValidate) {
        super(spacing);
    }
}
