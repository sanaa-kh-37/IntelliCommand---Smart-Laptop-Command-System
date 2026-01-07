package com.example.commandinterpreter;

import com.example.commandinterpreter.db.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        // Load login FXML (UI separated from logic)
        URL resource = getClass().getResource("/com/example/commandinterpreter/login.fxml");
        if (resource == null) {
            throw new IllegalStateException("Cannot find login.fxml");
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/commandinterpreter/styles.css").toExternalForm()
        );

        primaryStage.setTitle("IntelliCommand");
        primaryStage.setScene(scene);

        // 🔹 AUTO FULLSCREEN ON FIRST LAUNCH
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint(""); // Remove "Press ESC to exit fullscreen"

        // Load Poppins font (safe – optional)
        Font.loadFont(
                "https://fonts.googleapis.com/css2?family=Poppins:wght@700;700italic&display=swap",
                10
        );

        primaryStage.show();

        // Test DB (remove in production)
        new DatabaseHandler().testConnection();
    }


    public static void main(String[] args) {
        launch(args);

    }
}
