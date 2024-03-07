package org.example.TCP.ui;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.function.Consumer;

public class NIOClient {
    SocketChannel socketChannel;

    public NIOClient(String hostname, int port) throws IOException, InterruptedException {
        // 得到一个网络通道
        this.socketChannel = SocketChannel.open();
        // 设置非阻塞
        socketChannel.configureBlocking(false);

        // 提供服务器端的 IP 地址和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);
        // 连接服务器
        if (!socketChannel.connect(inetSocketAddress)) {
            Thread.sleep(10);
            if (!socketChannel.finishConnect()) {
                throw new IOException("服务端无法正常连接");
            }
        }
    }

    public void sendToServer(byte[] msg) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(msg);
        // 将 buffer 数据写入 channel
        socketChannel.write(buffer);
    }

    public void receiveFromServer(Consumer<byte[]> processor) throws IOException {
        // Create a new selector
        Selector selector = Selector.open();
        // Create a new ByteBuffer as the attachment
        socketChannel.register(selector, SelectionKey.OP_READ);
        while(true) {
            // Wait for events
            int numKeys = selector.select();

            if(numKeys > 0) {
                // Retrieve keys that have events ready
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();

                while(iter.hasNext()) {
                    SelectionKey key = iter.next();

                    if(key.isReadable()) {
                        // The socket channel has data to be read
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(256);
                        buffer.clear();
                        client.read(buffer);
                        buffer.flip();
                        // Process data in buffer...
                        byte[] byteArray = new byte[buffer.remaining()];
                        buffer.get(byteArray);
                        processor.accept(byteArray);
                    }

                    iter.remove();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        NIOClient nc = new NIOClient("localhost", 38888);
        new Thread(() -> {
            int i = 0;
            while (true){
                try {
                    Thread.sleep(1000);
                    i++;
                    nc.sendToServer((i+"s later").getBytes());
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        nc.receiveFromServer(bytes -> {
            System.out.println(new String(bytes));
        });
    }

}
