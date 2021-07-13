package ru.alexander_kramarenko.netty_file_transfer.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.alexander_kramarenko.netty_file_transfer.common.FileMessage;
import ru.alexander_kramarenko.netty_file_transfer.common.FileRequest;
import ru.alexander_kramarenko.netty_file_transfer.common.ServerFileDirectoryRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class MainHandler extends ChannelInboundHandlerAdapter {

    private final String CMD_UPDATE_SERVER_FILE_PANEL = "cmdUpdateServerDirectory";
    List<String> serverFileDirectory = new ArrayList<>();

    // На будущее - тестирование работы с поддиректорией клиента
    private String userName = "Steve";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // Некое подобие протокола обмена данными на основе сущностей
        // Проверяем при условии сущности Запроса на передачу файла

        if (msg instanceof FileRequest) {
            FileRequest fr = (FileRequest) msg;

            // Проверяем команду на обновление панели файлов сервера при запуске сцены JavaFX
            // В качестве команды подается FileRequest на файл с именем "cmdUpdateServerDirectory"

            if (fr.getFileName().equals(CMD_UPDATE_SERVER_FILE_PANEL)) {

                // Обновляем список файлов Server_storage
                refreshServerFilesList(ctx);
            }

            // Если пришла не команда, то вероятно, пришел запрос на скачивание файла
            // Проверяем наличие файла.
            // Если файл есть - отправляем в канал

            if (Files.exists(Paths.get("server_storage/" + fr.getFileName()))) {
                FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFileName()));
                ctx.writeAndFlush(fm);
            }
        }

        // Если msg из мущности FileMessage - значит пришел файл для заказчки на сервер

        if (msg instanceof FileMessage) {

            // Кастуем msg к fm1 и пишем файл в хранилище сервера
            FileMessage fm1 = (FileMessage) msg;
            Files.write(Paths.get("server_storage/" + fm1.getFileName()), fm1.getData(), StandardOpenOption.CREATE);


            refreshServerFilesList(ctx);
//            ServerFileDirectoryRequest sfdr = new ServerFileDirectoryRequest(serverFileDirectory);
//            ctx.writeAndFlush(sfdr);
        }
    }

// Мой код - собираем список файлов на сервере - начало
//  Обновляем список файлов Server_storage и отправляем в канал

    public void refreshServerFilesList(ChannelHandlerContext ctx1) throws IOException {
        serverFileDirectory.clear();
        Files.list(Paths.get("server_storage"))
                .filter(p -> !Files.isDirectory(p))
                .map(p -> p.getFileName().toString())
                .forEach(o -> serverFileDirectory.add(o));

        // Оборачиваем сообщение в ServerFileDirectoryRequest,
        // чтобы клиент понимал какую панель обновлять (сервера)

        ServerFileDirectoryRequest sfdr = new ServerFileDirectoryRequest(serverFileDirectory);
        ctx1.writeAndFlush(sfdr);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
