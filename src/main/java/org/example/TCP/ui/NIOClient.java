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
    ByteBuffer buffer;

    public NIOClient(String hostname, int port) throws IOException, InterruptedException {
        // 得到一个网络通道
        this.socketChannel = SocketChannel.open();
        // 设置非阻塞
        socketChannel.configureBlocking(false);
        this.buffer = ByteBuffer.allocate(256);

        // 提供服务器端的 IP 地址和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);
        // 连接服务器
        if (!socketChannel.connect(inetSocketAddress)) {
            Thread.sleep(10);
            if (!socketChannel.finishConnect()) {
                throw new IOException("服务端无法正常连接");
            }
        }
        System.out.println("客户端成功启动");
    }

    public void sendToServer(byte[] msg) throws IOException {
        buffer = ByteBuffer.wrap(msg);
        // 将 buffer 数据写入 channel
        socketChannel.write(buffer);
        buffer.clear();
    }

    public void receiveFromServer(Consumer<byte[]> processor) throws IOException {
        System.out.println("准备接收信息");
        while (true) {
            int bytesRead = -1;
            try {
                bytesRead = socketChannel.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (bytesRead == -1) break;
            if (bytesRead > 0){
                processor.accept(buffer.array());
                buffer.clear();
            }
        }
    }


}
