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

    public void run() throws Exception {

        // Два пула потоков Netty
        // mainGroup (часто bossGroup) - как правило один поток, ожидающий новых подключений клиентов
        // workerGroup - пул потоков обработки сетевой логики
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // Запуск ковеера сервера Netty (Bootstrap - запуск Netty на клиенте)
            ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    // Ограничиваем размер декодируемого файла
                                    new ObjectDecoder(50 * 1024 * 1024, ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    // Подключаем обработчик собственного производства
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


