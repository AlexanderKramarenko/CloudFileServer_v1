package ru.alexander_kramarenko.netty_file_transfer.common;

public class FileRequest extends AbstractMessage {
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public FileRequest(String fileName) {
        this.fileName = fileName;
    }
}
