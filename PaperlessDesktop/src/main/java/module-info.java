module com.paperlessdesktop {
    requires transitive javafx.controls;
    requires javafx.graphics;

    requires java.desktop;
    requires org.apache.commons.io;
    requires commons.logging;
    
    requires transitive java.logging;
    requires json.simple;
    requires javafx.fxml;
    requires tess4j;


    opens com.paperlessdesktop.gui to javafx.fxml;
    opens com.paperlessdesktop.gui.controllers to javafx.fxml;

    exports com.paperlessdesktop.gui;
    exports com.paperlessdesktop.gui.controllers;
    exports com.paperlessdesktop.finder;
    exports com.paperlessdesktop.finder.automation;
    exports com.paperlessdesktop.finder.fileloader;
}

