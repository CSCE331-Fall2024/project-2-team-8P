module org.example.pandaexpresspos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;

    opens org.example.pandaexpresspos to javafx.fxml;
    exports org.example.pandaexpresspos;
    exports org.example.pandaexpresspos.controllers;
    exports org.example.pandaexpresspos.database;
    exports org.example.pandaexpresspos.models;
    exports org.example.pandaexpresspos.models.wrappers;
    opens org.example.pandaexpresspos.controllers to javafx.fxml;
}