package org.example.TCP.ui;

import java.io.*;
import java.util.function.Consumer;

public class MessageServiceNIOImpl extends NIOClient implements MessageService<Message>{
    ByteArrayOutputStream out;
    ObjectOutputStream os;
    public MessageServiceNIOImpl(String hostname, int port) throws IOException, InterruptedException {
        super(hostname, port);
        this.out = new ByteArrayOutputStream();
        this.os = new ObjectOutputStream(out);

    }

    @Override
    public void send(Message msg) {
        try {
            this.os.writeObject(msg);
            sendToServer(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receive(Consumer<Message> processor) {
        try {
            receiveFromServer(bytes -> {
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
