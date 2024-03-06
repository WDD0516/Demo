package org.example.TCP.ui;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public class IOEventDrivenManager {
    private final Selector selector;

    public IOEventDrivenManager() throws IOException {
        this.selector = Selector.open();
    }

    public void registerChannel(SocketChannel socketChannel, Consumer<ByteBuffer> processor) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        socketChannel.configureBlocking(false);
        socketChannel.register(this.selector, SelectionKey.OP_READ, new Context(buffer, processor));
    }

    public void processEvents() throws IOException {
        while(true) {
            if(selector.select() > 0) {
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while(iter.hasNext()) {
                    SelectionKey key = iter.next();

                    if(key.isReadable()) {
                        Context context = (Context) key.attachment();
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = context.getBuffer();
                        buffer.clear();
                        channel.read(buffer);
                        buffer.flip();

                        // Process data in buffer using user provided processor
                        context.getProcessor().accept(buffer);

                        iter.remove();
                    }
                }
            }
        }
    }

    private static class Context {
        private ByteBuffer buffer;
        private Consumer<ByteBuffer> processor;

        public Context(ByteBuffer buffer, Consumer<ByteBuffer> processor) {
            this.buffer = buffer;
            this.processor = processor;
        }

        public ByteBuffer getBuffer() {
            return buffer;
        }

        public Consumer<ByteBuffer> getProcessor() {
            return processor;
        }
    }
}