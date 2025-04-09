package org.example.TCP;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class ChatServer {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private Map<SocketChannel, ByteBuffer> clients = new HashMap<>();

    public ChatServer() throws Exception {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(25000));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void runServer() throws Exception {
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    SocketChannel client = serverSocketChannel.accept();
                    client.configureBlocking(false);
                    client.register(selector, SelectionKey.OP_READ);

                    ByteBuffer buffer = ByteBuffer.allocate(256);
                    clients.put(client, buffer);

                    System.out.println("Connected to client at " + client.getRemoteAddress());
                }

                if (key.isReadable()) {
                    SocketChannel client = (SocketChannel) key.channel();
                    ByteBuffer buffer = clients.get(client);

                    int bytesRead = client.read(buffer);

                    if (bytesRead > 0) {
                        buffer.flip();

                        Iterator<SocketChannel> iterClient = clients.keySet().iterator();
                        while(iterClient.hasNext()) {
                            SocketChannel clientToSend = iterClient.next();
//                            if (!clientToSend.equals(client)) { // prevent to send message back to the sender
                                clientToSend.write(ByteBuffer.wrap(buffer.array(), 0, bytesRead));
//                            }
                        }
                        System.out.println("Message sent to all clients: " + new String(buffer.array(), 0, bytesRead));

                        buffer.clear();
                    }
                }

                iter.remove();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ChatServer().runServer();
    }
}