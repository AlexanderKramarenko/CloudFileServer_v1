package ru.alexander_kramarenko.netty_file_transfer.common;

import java.util.List;

// Общий класс, используемый на клиента и на сервере
// Формируем сообщение на обновление списка файлов сервера
// Отсылается только от сервера к клиенту, чтобы клиент знал какое окно обновлять (сервера)

public class ServerFileDirectoryRequest extends AbstractMessage {

    private List serverDirectory;

    public List getserverDirectory() {
        return serverDirectory;
    }

    public ServerFileDirectoryRequest(List serverDirectory) {
        this.serverDirectory = serverDirectory;
    }


}
