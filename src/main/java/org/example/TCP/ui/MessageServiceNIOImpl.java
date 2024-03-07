package org.example.TCP.ui;

import java.io.*;
import java.util.Arrays;
import java.util.function.Consumer;

public class MessageServiceNIOImpl implements MessageService<Message>{
    ByteArrayOutputStream out;
    ObjectOutputStream os;
    NIOClient nioClient;
    public MessageServiceNIOImpl(String hostname, int port) throws IOException, InterruptedException {
        this.out = new ByteArrayOutputStream();
        this.os = new ObjectOutputStream(out);
        this.nioClient = new NIOClient(hostname, port);
    }

    @Override
    public void send(Message msg) {
        try {
            this.os.writeObject(msg);
            byte[] bytes = out.toByteArray();
            nioClient.sendToServer(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receive(Consumer<Message> processor) {
        try {
            nioClient.receiveFromServer(bytes -> {
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                try {
                    ObjectInputStream is = new ObjectInputStream(in);
                    Message msg = (Message)is.readObject();
                    processor.accept(msg);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
