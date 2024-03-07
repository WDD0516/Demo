package org.example.TCP.ui;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.function.Consumer;

public class IOEventDrivenManagerContext {
    private ByteBuffer buffer;
    private final Consumer<SelectionKey> processor;

    public IOEventDrivenManagerContext(Consumer<SelectionKey> processor) {
        this.processor = processor;
    }

    public IOEventDrivenManagerContext(ByteBuffer buffer, Consumer<SelectionKey> processor) {
        this.buffer = buffer;
        this.processor = processor;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public Consumer<SelectionKey> getProcessor() {
        return processor;
    }
}
