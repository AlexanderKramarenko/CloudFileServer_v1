package ru.alexander_kramarenko.netty_file_transfer.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import ru.alexander_kramarenko.netty_file_transfer.common.AbstractMessage;
import ru.alexander_kramarenko.netty_file_transfer.common.FileMessage;
import ru.alexander_kramarenko.netty_file_transfer.common.FileRequest;
import ru.alexander_kramarenko.netty_file_transfer.common.ServerFileDirectoryRequest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    private final String CMD_UPDATE_SERVER_FILE_PANEL = "cmdUpdateServerDirectory";


    @FXML
    ListView<String> clientFilesList;
    @FXML
    TextField fileNameForDownload;

    @FXML
    TextField fileNameForUpload;

    @FXML
    ListView<String> serverFilesList;

    @FXML
    TableView<String> filesTable;

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

                    if (am instanceof ServerFileDirectoryRequest) {
                        ServerFileDirectoryRequest lst = (ServerFileDirectoryRequest) am;
                        refreshServerFilesList(lst);
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
        requestRefreshServerPanelFileList();
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {

        if (fileNameForDownload.getLength() > 0) {
            Network.sendMsg(new FileRequest(fileNameForDownload.getText()));
            fileNameForDownload.clear();
        }
    }

    public void pressOnUploadBtn(ActionEvent actionEvent) {

        if (fileNameForUpload.getLength() > 0) {
            try {

                FileRequest fr = new FileRequest(fileNameForUpload.getText());
                FileMessage fm = new FileMessage(Paths.get("client_storage/" + fr.getFileName()));
                Network.sendMsg(fm);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileNameForUpload.clear();
        }
    }

    public void refreshLocalFilesList() {
        Platform.runLater(() -> {
            try {
                clientFilesList.getItems().clear();
                Files.list(Paths.get("client_storage"))
                        .filter(p -> !Files.isDirectory(p))
                        .map(p -> p.getFileName().toString())
                        .forEach(o -> clientFilesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void refreshServerFilesList(ServerFileDirectoryRequest lst) {
        Platform.runLater(() -> {
            // Обновление серверной панели из полученного листа
            serverFilesList.getItems().clear();
            serverFilesList.getItems().addAll(lst.getserverDirectory());
        });
    }

    public void selectFileNameFromClientList() {
        clientFilesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    fileNameForUpload.setText(clientFilesList.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    public void selectFileNameFromServerList() {
        serverFilesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    fileNameForDownload.setText(serverFilesList.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    public void requestRefreshServerPanelFileList() {
        FileRequest fr = new FileRequest(CMD_UPDATE_SERVER_FILE_PANEL);
        Network.sendMsg(fr);
    }
}
