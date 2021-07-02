package ru.alexander_kramarenko.netty_file_transfer.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.alexander_kramarenko.netty_file_transfer.common.AbstractMessage;

import java.io.IOException;
import java.net.Socket;

public class Network {

    private static Socket socket;
    private static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;

    public static void start() {

        // Клиент работает на Nio
        // Запускаем канал клиента и ограничиваем размер входящего сообщения

        try {
            socket = new Socket("localhost", 8189);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), 50 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Последовательно останавливаем службы

    public static void stop() {

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для отправки сообщений

    public static boolean sendMsg(AbstractMessage msg) {
        try {
            out.writeObject(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Метод для приема сообщений

    public static AbstractMessage readObject() throws ClassNotFoundException, IOException {
        Object obj = in.readObject();
        return (AbstractMessage) obj;
    }
}
