package com.fluentwind.tt.socketapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onStop() {
        super.onStop();
        socketClient.close();
    }

    private EditText editText;
    private SocketClient socketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        editText=(EditText)findViewById(R.id.editText);
        Button button_connect=(Button)findViewById(R.id.button_connect);
        button_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socketClient instanceof  SocketClient){
                    socketClient.connect();

                }else{

                    socketClient=new SocketClient("192.168.1.101",5008);
                    socketClient.setOnConnectListener(new OnConnectListener() {
                        @Override
                        public void onTimeOut() {

                            Toast.makeText(getApplicationContext(),"连接超时！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onConnected() {

                            Toast.makeText(getApplicationContext(),"连接成功！",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
socketClient.send(editText.getText().toString());
                //socketClient.send(editText.getText().toString());



            }
        });
    }
}
