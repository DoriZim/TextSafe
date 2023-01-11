module bepo.textsafe.textsafe {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens bepo.textsafe.textsafe to javafx.fxml;
    exports bepo.textsafe.textsafe;
    exports bepo.textsafe.textsafe.controller;
    opens bepo.textsafe.textsafe.controller to javafx.fxml;
    exports bepo.textsafe.textsafe.views to javafx.fxml;
    opens bepo.textsafe.textsafe.views;
}