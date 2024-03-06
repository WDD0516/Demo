package org.example.TCP.ui;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
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
        IOEventDrivenManager manager = new IOEventDrivenManager();
        manager.registerChannel(socketChannel, (buffer) -> {
            // Process data in buffer...
            byte[] byteArray = new byte[buffer.remaining()];
            buffer.get(byteArray);
            processor.accept(byteArray);
        });
        // Start processing events
        manager.processEvents();
    }

}
