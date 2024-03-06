package org.example.TCP.ui;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.function.Consumer;

public class MessageServiceTCPImpl implements MessageService<Message>{
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    public MessageServiceTCPImpl(Socket socket) throws IOException {
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        PrintWriter headWriter = new PrintWriter(os, true);
        UUID uuid = UUID.randomUUID();
        headWriter.println(uuid);
        BufferedReader headReader = new BufferedReader(new InputStreamReader(is));
        String response = headReader.readLine();
        if(response.equals("1")){
            throw new RuntimeException("连接失败，请重新启动客户端");
        }else{
            System.out.println(uuid+"已注册");
        }
        this.socket = socket;
        this.oos = new ObjectOutputStream(os);
        this.ois = new ObjectInputStream(is);
    }

    @Override
    public void send(Message msg) {
        try{
            oos.writeObject(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void receive(Consumer<Message> processor) {
        while(true){
            Message msg;
            try {
                msg = (Message) ois.readObject();
                processor.accept(msg);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
