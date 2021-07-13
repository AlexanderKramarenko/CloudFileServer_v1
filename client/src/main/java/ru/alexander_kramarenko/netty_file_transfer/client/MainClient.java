package ru.alexander_kramarenko.netty_file_transfer.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("File Server terminal");
        primaryStage.setScene(new Scene(root, 550, 600));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
