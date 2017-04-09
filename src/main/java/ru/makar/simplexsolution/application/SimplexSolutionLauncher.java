package ru.makar.simplexsolution.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class SimplexSolutionLauncher extends Application {
    @Override
    public void start(Stage stage) {
        try {
            AnchorPane root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/main-layout.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
            stage.setScene(scene);
            stage.getIcons().add(new Image(getClass().getClassLoader().getResource("img/icon.png").toExternalForm()));
            stage.setTitle("Simplex Solution");
            stage.show();
            stage.setResizable(false);
            stage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
