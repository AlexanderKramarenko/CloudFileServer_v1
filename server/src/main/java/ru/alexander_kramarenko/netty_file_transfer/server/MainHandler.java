package ru.alexander_kramarenko.netty_file_transfer.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.alexander_kramarenko.netty_file_transfer.common.FileMessage;
import ru.alexander_kramarenko.netty_file_transfer.common.FileRequest;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MainHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //       super.channelReadComplete(ctx);
        if (msg instanceof FileRequest){
            FileRequest fr = (FileRequest) msg;

            if (Files.exists(Paths.get("server_storage/" + fr.getFileName()))) {
                FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFileName()));
                ctx.writeAndFlush(fm);
            }
        }

        if (msg instanceof FileMessage){

        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
