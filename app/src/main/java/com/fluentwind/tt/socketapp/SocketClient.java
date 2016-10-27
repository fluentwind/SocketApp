package com.fluentwind.tt.socketapp;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;


/**
 * Created by Administrator on 2016/10/12.
 */
public class SocketClient {
    private Socket socket;
    private PrintWriter printWriter;
    private  Thread mainThread,listenThread,sendThread ;
    private BufferedReader reader;
    private  OnConnectListener onConnectListener;
    private Handler handler;
    public String Ip;
    private int Port;
    public SocketClient( String ip,  int port)  {
        Ip=ip;
        Port=port;
        connect();
    }

    public void setOnConnectListener(final OnConnectListener onConnectListener) {
        this.onConnectListener = onConnectListener;
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        onConnectListener.onConnected();
                        break;
                    case 2:
                        onConnectListener.onTimeOut();
                        break;
                }

            }
        };
    }

    public String getIp() {
        return Ip;
    }

    public int getPort() {
        return Port;
    }

    public void connect(){
        mainThread =new Thread(){

            @Override
            public void run() {

                try {
                    System.out.println("connecting");

                    socket = new Socket();
                    SocketAddress address = new InetSocketAddress(Ip, Port);
                    try {
                        socket.connect(address, 2000);
                        if (onConnectListener!=null){

                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);


                        }
                        System.out.println("connected");
                        OutputStream outputStream= null;
                        outputStream = socket.getOutputStream();
                        reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        printWriter=new PrintWriter(outputStream);
                        receive();
                    } catch (SocketTimeoutException e) {
                        if (onConnectListener!=null){
                            Message message = new Message();
                            message.what = 2;
                            handler.sendMessage(message);
                        }
                        System.out.println("timeout");


                        close();

                    }


                    /*socket = new Socket(Ip, Port);
                    socket.setSoTimeout(2000);*/

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        mainThread.start();
    }

    private void receive(){

        listenThread=new Thread(){
            @Override
            public void run() {
                while (true) {

                    String get = null;

                    try {
                        get = reader.readLine();
                    } catch (IOException e) {


                        break;
                    }

                    if (get!=null){
                        System.out.println("get:" + get);
                    }else {
                        break;
                    }

                }
                close();

            }
        };
        listenThread.start();

    }

    public void rest(){

        close();
        System.out.println("rest" );
        connect();
    }
    public void send(final String msg){
        if (socket!=null){
            if (socket.isConnected() ){
                sendThread=new Thread(){
                    @Override
                    public void run() {
                        printWriter.println(msg);
                        printWriter.flush();
                        System.out.println(msg + " send finished" );

                    }
                };
                sendThread.start();
            }

        }



    }

    public void close()  {


        /*if (listenThread.isAlive()) {
            listenThread.interrupt();
        }*/
        if (!socket.isClosed()){
            try {
                socket.close();
                System.out.println("closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public String getState() {
        String state="no state";
        if (socket.isClosed()){
            state="Closed!";
        }else if(socket.isConnected()){
            state="Connected!";
        }
        return state;
    }


}
