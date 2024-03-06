package org.example.TCP;

import java.io.*;
import java.net.Socket;

//传递json 包括用户名（唯一） 发送时间 发送内容
//客户端解析
public class TCPClient {


    public static void main(String[] args) {
        String user = args[0];
        try {
            //创建Socket对象，指定连接的服务器端地址和端口号
            Socket socket = new Socket("119.29.55.185", 38888);
            System.out.println("已连接服务器！");

            PrintWriter headWriter = new PrintWriter(socket.getOutputStream(), true);
            headWriter.println(user);
            BufferedReader headReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response = headReader.readLine();
            if(response.equals("Fail")){
                throw new RuntimeException("该用户已存在，请修改user");
            }else{
                System.out.println("用户"+user+"已注册");
            }

            //启动读取服务器端消息的子线程

            Thread thread = new Thread(() -> {
                try {
                    //获取输入流，接收数据
                    ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                    while (true) {
                        Thread.sleep(1000);
                        Message message = (Message) ois.readObject();
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("服务器内部异常,输入流关闭");
                } catch (InterruptedException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

            //启动发送消息的主线程
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            //获取输出流，向服务器端发送数据
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

//
            while (true) {
                Thread.sleep(1000);
                String content = reader.readLine();
                Message message = new Message(user, content);
                oos.writeObject(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器内部异常,输出流关闭");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}