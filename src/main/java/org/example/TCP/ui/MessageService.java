package org.example.TCP.ui;

import java.util.function.Consumer;

public interface MessageService<T> {
    void send(T msg);

    void receive(Consumer<T> processor);
}
