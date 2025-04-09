package org.example.TCP;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

public class ChatClient {
    private SocketChannel client;
    private ByteBuffer buffer;
    private ChatClientListener listener;

    public ChatClient() throws IOException {
        client = SocketChannel.open(new InetSocketAddress("localhost", 25000));
        buffer = ByteBuffer.allocate(256);
        buffer.clear();
        listener = new ChatClientListener();
    }

    private void send(String message) throws IOException {
        buffer = ByteBuffer.wrap(message.getBytes());
        client.write(buffer);
        buffer.clear();
    }

    private void receive() throws IOException {
        while (true) {
            int bytesRead = -1;
            try {
                bytesRead = client.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bytesRead == -1) break;

            System.out.println("Message received: " + new String(buffer.array()).trim());
            buffer.clear();
        }
    }

    class ChatClientListener extends Thread {
        public void run() {
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String message;
                try {
                    message = console.readLine();

                    send(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.listener.start();
        client.receive();
    }
}