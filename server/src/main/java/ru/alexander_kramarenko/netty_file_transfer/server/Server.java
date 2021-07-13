package ru.alexander_kramarenko.netty_file_transfer.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Server {

    private final int DECODER_LIMIT_X_SIZE = 1024;
    private final int DECODER_LIMIT_Y_SIZE = 1024;
    private final int DECODER_LIMIT_Z_SIZE = 50;

    public void run() throws Exception {

        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new ObjectDecoder(DECODER_LIMIT_X_SIZE * DECODER_LIMIT_Y_SIZE * DECODER_LIMIT_Z_SIZE, ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new MainHandler()
                            );
                        }
                    });
            ChannelFuture future = b.bind(8189).sync();
            future.channel().closeFuture().sync();

        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new Server().run();
    }
}


