package org.example.TCP;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

public class TCPService {
    public static void main(String[] args) {
        HashMap<String,ObjectOutputStream> usermap = new HashMap<>();
        try {
            //创建ServerSocket对象，绑定端口号
            ServerSocket serverSocket = new ServerSocket(38888);
            System.out.println("服务器启动成功，等待客户端连接...");

            while(true) {
                //等待客户端连接，阻塞式方法
                Socket socket = serverSocket.accept();
                System.out.println("客户端 " + socket + " 已连接！");
                BufferedReader headReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String u = headReader.readLine();
                PrintWriter headWriter = new PrintWriter(socket.getOutputStream(), true);
                if(usermap.containsKey(u)){
                    System.out.println("该用户已存在，断开此客户端连接");
                    headWriter.println("Fail");
                    headWriter.close();
                    headReader.close();
                    continue;
                }else{
                    headWriter.println("Success");
                }
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                usermap.put(u,oos);
                //启动读取客户端消息的子线程
                Thread thread = new Thread(() -> {
                    try {
                        //获取输入流，接收数据
                        InputStream sis = socket.getInputStream();
                        ObjectInputStream ois = new ObjectInputStream(sis);
                        while (true) {
                            Thread.sleep(1000);
                            Message message = null;
//                            if (socket.){
//                                System.out.println(u);
//                                usermap.remove(u);
//                                break;
//                            }
                            try{
                                message = (Message) ois.readObject();
                            }catch (SocketException | EOFException e){
                                System.out.println(u+"已断开");
                                usermap.remove(u);
                                break;
                            }
                            //获取输出流，向服务器端分发数据
                            for(String user:usermap.keySet()){
                                try {
                                    usermap.get(user).writeObject(message);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
                thread.start();
                System.out.println(usermap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}