package org.example.TCP.ui;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
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

    public void registerChannel(SelectableChannel socketChannel, int ops, IOEventDrivenManagerContext context) throws IOException {
        socketChannel.configureBlocking(false);
        socketChannel.register(this.selector, ops, context);
    }

    public void processEvents() throws IOException {
        while(true) {
            if(selector.select() > 0) {
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while(iter.hasNext()) {
                    SelectionKey key = iter.next();

                    IOEventDrivenManagerContext context = (IOEventDrivenManagerContext) key.attachment();
                    // Process data in buffer using user provided processor
                    context.getProcessor().accept(key);
                    iter.remove();

                }
            }
        }
    }
}