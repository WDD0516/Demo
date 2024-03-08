package org.example.TCP.ui;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;

public class NIOServer {
    public static void main(String[] args) throws Exception {
        HashMap<SocketChannel,ByteBuffer> map = new HashMap<>();

        // 创建ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 得到一个Selector对象
        Selector selector = Selector.open();

        // 绑定一个端口号，启动服务器
        serverSocketChannel.socket().bind(new InetSocketAddress(38888));

        // 设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        // 把ServerSocketChannel注册到Selector， 关心OP_ACCEPT事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if(selector.select(1000) == 0) {
                continue;
            }

            // 获取SelectionKeys, 如果返回的>0, 表示已经获取到关注的事件
            // 通过SelectionKeys获取相应Channel的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                try{
                    if(key.isAcceptable()) { // 有新的客户端连接
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        map.put(socketChannel, ByteBuffer.allocate(256));
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    if(key.isReadable()) {  // 发生OP_READ
                        SocketChannel channel = (SocketChannel)key.channel();
                        ByteBuffer buffer = map.get(channel);
                        buffer.clear();
                        int bytesRead = channel.read(buffer);
                        for (SocketChannel sc : map.keySet()) {
//                            if (channel != sc) {
                                sc.write(ByteBuffer.wrap(buffer.array(),0,bytesRead));
//                            }
                        }

//                        if (read > 0){
//                            ArrayList<SocketChannel> del = new ArrayList<>();
//                            for(SocketChannel sc:list){
//                                sc.write(buffer);
//                            }
//                        }

                    }
                }catch (IOException e){
                    key.cancel();
                    key.channel().close();
                }
                iterator.remove(); // 手动从集合种移动当前的selectionKey, 防止重复操作
            }
        }
    }
}