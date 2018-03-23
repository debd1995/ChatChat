package com.wgt.chatchat;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView chatList;
    private EditText et_msg;
    private Button btn_send;
    private Socket socket;

    private DataInputStream din;
    private DataOutputStream dout;

    private ChatAdapter chatAdapter;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initViews();
        socket = SocketHandler.getSocket();
        if (socket == null) {
            Toast.makeText(this, "ERROR : no socket found", Toast.LENGTH_LONG).show();
        } else {
            try {
                din = new DataInputStream(socket.getInputStream());
                dout = new DataOutputStream(socket.getOutputStream());

                new ReadMessage().start();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to create Input/Output Stream", Toast.LENGTH_SHORT).show();
            }
        }

        handler = new Handler(Looper.getMainLooper());
    }


    private void initViews() {
        chatList = findViewById(R.id.recycler_list);
        et_msg = findViewById(R.id.et_msg);
        btn_send = findViewById(R.id.btn_send);

        btn_send.setOnClickListener(this);

        chatAdapter = new ChatAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatList.setLayoutManager(linearLayoutManager);
        chatList.setAdapter(chatAdapter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                new SendMessage(et_msg.getText().toString()).start();
                break;
        }
    }

    private class SendMessage extends Thread {
        String message;
        public SendMessage(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                dout.writeUTF(message);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int i =chatAdapter.addChat(new ChatModel(message, true));
                        //chatList.scrollToPosition(i);
                        et_msg.setText("");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private class ReadMessage extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    final String data = din.readUTF();
                    if (data != null && !data.equals("")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                int i = chatAdapter.addChat(new ChatModel(data, false));
                                //chatList.scrollToPosition(i);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatActivity.this, "Failed to read message", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

}
