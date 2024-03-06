package org.example.TCP.ui;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

import static java.awt.Color.WHITE;

public class Chat {
    JFrame frame;
    JButton btn;
    JTextPane panel;
    JTextField text;
    JScrollPane scrollPane;
    JScrollBar jscrollBar;
    String me;

    MessageService<Message> ms;

    public Chat(String me) throws Exception {
        this(me, new MessageService<Message>() {
            @Override
            public void send(Message msg) {

            }

            @Override
            public void receive(Consumer<Message> processor) {

            }
        });
    }



    public Chat(String me,MessageService<Message> ms) throws Exception {

        this.ms = ms;
        this.me = me;

        frame = new JFrame();//建一个窗口
        frame.setSize(414, 337);//窗口大小，宽：414，高：337
        frame.setLayout(new LayoutManager(){

            @Override
            public void addLayoutComponent(String name, Component comp) {

            }

            @Override
            public void removeLayoutComponent(Component comp) {

            }

            @Override
            public Dimension preferredLayoutSize(Container parent) {
                return null;
            }

            @Override
            public Dimension minimumLayoutSize(Container parent) {
                return null;
            }

            @Override
            public void layoutContainer(Container parent) {

                int w = parent.getWidth();//parent.getWidth()是在拉伸窗口时获得窗口的宽
                int h = parent.getHeight();//parent.getHeight()是在拉伸窗口是获得窗口的高
//            double x = w*(10.0/400);//10代表按钮的x=10
//            double y = h*(10.0/300);//10代表按钮的y=10
//            double btnw = 61.0/400*w;//61代表按钮的宽=61
//            double btnh = 28.0/300*h;//28代表按钮的高=28
                //上面四行代码用下面一张图片解释
//                panel.setBounds(5,5,w-10,h-100);
                scrollPane.setBounds(5,5,w-10,h-100);
                text.setBounds(5,h-88,w-10,40);
                btn.setBounds(w-70, h-40, 61, 28);
            }
        });//匿名内部类，实现了LayoutManager接口，也就是我自己定义自己的布局管理器，
        frame.setMinimumSize(new Dimension(414,337));//最小

        if(me.length() > 8){
            JOptionPane.showMessageDialog(frame,"名称过长！");
            throw new Exception("名称过长！");
        }



        panel = new JTextPane();
        panel.setOpaque(true);//背景非透明
        panel.setBackground(WHITE);

        scrollPane = new JScrollPane(panel);
        frame.add(scrollPane);


        //        panel.setCaretPosition(panel.getStyledDocument().getLength());


        text = new JTextField(20);
        text.setOpaque(true);//背景非透明
        text.setBackground(WHITE);
        frame.add(text);

        btn = new JButton("发送");//创建一个按钮
        //点击发送
        btn.addActionListener(e -> {
            if (e.getSource()==btn){
                String msg = text.getText();
                if(!msg.equals("")){
                    sendMessage(msg);
                }else{
                    JOptionPane.showMessageDialog(frame,"不能发送空内容！");
                }
            }
        });

        //回车发送
        text.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String msg = text.getText();
                    if(!msg.equals("")){
                        sendMessage(msg);
                    }else{
                        JOptionPane.showMessageDialog(frame,"不能发送空内容！");
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        frame.add(btn);//将按钮添加进窗口

        frame.setVisible(true);//让窗口显示
        frame.setLocationRelativeTo(null);//设置窗口居于屏幕中间显示
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    private void showMessage(String user, String msg, boolean fromSelf){
        SimpleAttributeSet att = new SimpleAttributeSet();
//        StyleConstants.setFontFamily(att, "仿宋");
        StyleConstants.setFontSize(att,13);
        Document docs = panel.getDocument();
        try {
            if(fromSelf) {
                StyleConstants.setForeground(att, Color.MAGENTA);
            }else {
                StyleConstants.setForeground(att, Color.red);
            }
            String user_info = user+"  ";
            docs.insertString(docs.getLength(), user_info, att);
            long time = System.currentTimeMillis();
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(time);
            String time_info = formatter.format(date)+":\n";//发送时间：绿色
            StyleConstants.setForeground(att, Color.green);
            docs.insertString(docs.getLength(), time_info, att);
            String msg_info = " "+msg+"\n";//发送内容：黑色
            StyleConstants.setFontSize(att,12);
            StyleConstants.setForeground(att, Color.black);
            docs.insertString(docs.getLength(), msg_info, att);

            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        text.setText("");
        // 必须设置在jframe添加完组件之后
        jscrollBar = scrollPane.getVerticalScrollBar();
        if (jscrollBar != null) {
            // 必须先获取一次jscrollBar.getMaximum()，否则滚动不到最底部, swing bug
            jscrollBar.setValue(jscrollBar.getMaximum());
        }

    }

    private void sendMessage(String msg){
        if(msg.equals("clear")){
            panel.setText("");
        }else{
            ms.send(new Message(this.me,msg));
            this.showMessage(this.me,msg,true);
        }
    }

    private void receiveMessage(){
        this.ms.receive(msg -> {
            if (msg!=null){
                this.showMessage(msg.user,msg.context,false);
            }
        });
    }



    public static void main(String[] args) throws Exception {
        String[] addr = args[0].split(":");
        String user = args[1];
        Socket socket = new Socket(addr[0], Integer.parseInt(addr[1]));
        MessageServiceTCPImpl msti = new MessageServiceTCPImpl(socket);
        Chat chat = new Chat(user,msti);
        new Thread(chat::receiveMessage).start();
    }
}