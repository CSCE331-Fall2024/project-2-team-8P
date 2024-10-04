module org.example.pandaexpresspos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.pandaexpresspos to javafx.fxml;
    exports org.example.pandaexpresspos;
    exports org.example.pandaexpresspos.controllers;
    opens org.example.pandaexpresspos.controllers to javafx.fxml;
}