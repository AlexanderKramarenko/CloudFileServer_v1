package ru.alexander_kramarenko.netty_file_transfer.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import ru.alexander_kramarenko.netty_file_transfer.common.AbstractMessage;
import ru.alexander_kramarenko.netty_file_transfer.common.FileMessage;
import ru.alexander_kramarenko.netty_file_transfer.common.FileRequest;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;



public class MainController implements Initializable {

    @FXML
    ListView<String> filesList;
    @FXML
    TextField fileNameForDownload;

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Network.start();
        Thread t = new Thread(() -> {

            try {
                while (true) {
                    AbstractMessage am = Network.readObject();
                    if (am instanceof FileMessage) {


                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("client_storage/" + fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();

    }
        public void pressOnDownloadBtn (ActionEvent actionEvent){

            if (fileNameForDownload.getLength() > 0) {
                Network.sendMsg(new FileRequest(fileNameForDownload.getText()));
                fileNameForDownload.clear();
            }
        }

    public void refreshLocalFilesList() {
        Platform.runLater(() -> {
            try {
                filesList.getItems().clear();

                Files.list(Paths.get("client_storage"))
                        .filter(p -> !Files.isDirectory(p))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> filesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

    }

}




