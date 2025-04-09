package org.example.TCP.ui;

import java.io.IOException;

public class Test2 {
    public static void main(String[] args) throws IOException, InterruptedException {
        NIOClient nc = new NIOClient("localhost", 38888);
        int i = 0;
        while (true){
            Thread.sleep(1000);
            i++;
            String s = i+"s later";
            System.out.println(s);
            nc.sendToServer(s.getBytes());
        }
    }
}
