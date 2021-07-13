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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FileRequest) {
            FileRequest fr = (FileRequest) msg;

            if (fr.getFileName().equals(CMD_UPDATE_SERVER_FILE_PANEL)) {

                refreshServerFilesList(ctx);
            }

            if (Files.exists(Paths.get("server_storage/" + fr.getFileName()))) {
                FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFileName()));
                ctx.writeAndFlush(fm);
            }
        }

        if (msg instanceof FileMessage) {

            FileMessage fm1 = (FileMessage) msg;
            Files.write(Paths.get("server_storage/" + fm1.getFileName()), fm1.getData(), StandardOpenOption.CREATE);

            refreshServerFilesList(ctx);
        }
    }

    public void refreshServerFilesList(ChannelHandlerContext ctx1) throws IOException {
        serverFileDirectory.clear();
        Files.list(Paths.get("server_storage"))
                .filter(p -> !Files.isDirectory(p))
                .map(p -> p.getFileName().toString())
                .forEach(o -> serverFileDirectory.add(o));

        ServerFileDirectoryRequest sfdr = new ServerFileDirectoryRequest(serverFileDirectory);
        ctx1.writeAndFlush(sfdr);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
