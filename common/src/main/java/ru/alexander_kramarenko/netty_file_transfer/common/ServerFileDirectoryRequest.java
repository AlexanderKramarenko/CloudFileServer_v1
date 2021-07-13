package ru.alexander_kramarenko.netty_file_transfer.common;

import java.util.List;

public class ServerFileDirectoryRequest extends AbstractMessage {

    private List<String> serverDirectory;

    public List<String> getserverDirectory() {
        return serverDirectory;
    }

    public ServerFileDirectoryRequest(List<String> serverDirectory) {
        this.serverDirectory = serverDirectory;
    }

}
