package ekene.dian.librarydemofx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();
            
            // Set up the scene with a reasonable default size
            Scene scene = new Scene(root, 900, 600);
            
            // Apply the CSS
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            
            // Configure the stage
            primaryStage.setTitle("Library Management System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            
            // Set up uncaught exception handler
            Thread.setDefaultUncaughtExceptionHandler(HelloApplication::showError);
            
            // Show the stage
            primaryStage.show();
            
        } catch (Exception e) {
            showError(Thread.currentThread(), e);
            Platform.exit();
        }
    }
    
    private static void showError(Thread t, Throwable e) {
        System.err.println("An unexpected error occurred: " + e.getMessage());
        e.printStackTrace();
        
        // Show error dialog if we're on the JavaFX Application Thread
        if (Platform.isFxApplicationThread()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An unexpected error occurred");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}