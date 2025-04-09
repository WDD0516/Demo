package org.example.TCP.ui;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        NIOClient nc = new NIOClient("localhost", 38888);
        nc.receiveFromServer(bytes -> {
            System.out.println(new String(bytes).trim());
        });
    }
}
