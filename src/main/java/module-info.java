module ekene.dian.librarydemofx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    
    // Spring and Jackson dependencies
    requires spring.web;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens ekene.dian.librarydemofx to javafx.fxml;
    opens ekene.dian.librarydemofx.model to com.fasterxml.jackson.databind, javafx.base;
    
    exports ekene.dian.librarydemofx;
    exports ekene.dian.librarydemofx.model;
    exports ekene.dian.librarydemofx.service;
}