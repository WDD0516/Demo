package org.example.TCP.ui;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

public class TCPServer {
    public static void main(String[] args) {
        HashMap<String,ObjectOutputStream> usermap = new HashMap<>();
        try {
            //创建ServerSocket对象，绑定端口号
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            System.out.println("服务器启动成功，等待客户端连接...");

            while(true) {
                //等待客户端连接，阻塞式方法
                Socket socket = serverSocket.accept();
                System.out.println("客户端 " + socket + " 已连接！");

                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                BufferedReader headReader = new BufferedReader(new InputStreamReader(is));
                String u = headReader.readLine();
                PrintWriter headWriter = new PrintWriter(os, true);
                if(usermap.containsKey(u)){
                    headWriter.println("1");
                    headWriter.close();
                    headReader.close();
                    continue;
                }else{
                    headWriter.println("0");
                }
                ObjectOutputStream oos = new ObjectOutputStream(os);
                usermap.put(u,oos);
                //启动读取客户端消息的子线程
                Thread thread = new Thread(() -> {
                    try {
                        //获取输入流，接收数据
                        ObjectInputStream ois = new ObjectInputStream(is);
                        while (true) {
                            Thread.sleep(1000);
                            Message message;
                            try{
                                message = (Message) ois.readObject();
                            }catch (SocketException | EOFException e){
                                System.out.println(u+"已断开");
                                usermap.remove(u);
                                break;
                            }
                            //获取输出流，向服务器端分发数据
                            for(String user:usermap.keySet()){
                                if(!user.equals(u)){
                                    try {
                                        usermap.get(user).writeObject(message);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
