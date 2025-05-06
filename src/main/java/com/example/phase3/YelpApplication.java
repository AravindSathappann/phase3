package com.example.phase3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class YelpApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(YelpApplication.class.getResource("Yelp-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 960);
            scene.getStylesheets()
                    .add(YelpApplication.class.getResource("styles/styles.css").toExternalForm());
            stage.setTitle("YelpApp!");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}