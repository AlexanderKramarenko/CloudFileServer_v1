package ru.alexander_kramarenko.netty_file_transfer.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.alexander_kramarenko.netty_file_transfer.common.AbstractMessage;

import java.io.IOException;
import java.net.Socket;

public class Network {

    private static final int DECODER_LIMIT_X_SIZE = 1024;
    private static final int DECODER_LIMIT_Y_SIZE = 1024;
    private static final int DECODER_LIMIT_Z_SIZE = 50;

    private static Socket socket;
    private static ObjectEncoderOutputStream out;
    private static ObjectDecoderInputStream in;

    public static void start() {

        try {
            socket = new Socket("localhost", 8189);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), DECODER_LIMIT_X_SIZE * DECODER_LIMIT_Y_SIZE * DECODER_LIMIT_Z_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static boolean sendMsg(AbstractMessage msg) {
        try {
            out.writeObject(msg);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static AbstractMessage readObject() throws ClassNotFoundException, IOException {
        Object obj = in.readObject();
        return (AbstractMessage) obj;
    }
}
