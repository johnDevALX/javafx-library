module ekene.dian.librarydemofx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens ekene.dian.librarydemofx to javafx.fxml;
    exports ekene.dian.librarydemofx;
}