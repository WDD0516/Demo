package org.example.TCP.ui;

import java.io.Serializable;

public class Message implements Serializable {
    String user;
    String context;

    public Message(String user, String context) {
        this.user = user;
        this.context = context;
    }
}
