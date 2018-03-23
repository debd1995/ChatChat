package com.wgt.chatchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText et_ip, et_port, et_username;
    private Spinner spnr_type;
    private Button btn_go;

    private String[] type = {"Connect to Server", "Host Server"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        et_ip = findViewById(R.id.et_ip);
        et_port = findViewById(R.id.et_port);
        et_username = findViewById(R.id.et_username);
        spnr_type = findViewById(R.id.spnr_type);
        btn_go = findViewById(R.id.btn_go);

        ArrayAdapter spnrAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, type);
        spnrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_type.setAdapter(spnrAdapter);

        spnr_type.setOnItemSelectedListener(this);
        btn_go.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_go:
                if (btn_go.getText().equals("Connect")) {
                    connectToServer();
                } else if (btn_go.getText().equals("Start")) {
                    startServer();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String option = (String) parent.getSelectedItem();
        if (option.equals("Connect to Server")) {
            et_ip.setText("");
            et_ip.setEnabled(true);
            btn_go.setText("Connect");
        } else if (option.equals("Host Server")) {
            et_ip.setText(getServerIP());
            et_ip.setEnabled(false);
            btn_go.setText("Start");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private String getServerIP() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    private void connectToServer() {
        String ip = "";
        int port = 0;
        String uname = "";
        try {
            ip = et_ip.getText().toString();
            port = Integer.parseInt(et_port.getText().toString());
            uname = et_username.getText().toString();

            ClientAsync clientAsync = new ClientAsync(this, ip, port, uname);
            clientAsync.execute();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR : invalid port", Toast.LENGTH_SHORT).show();
        }
    }

    private void startServer() {
        String ip = "";
        int port = 0;
        String uname = "";
        try {
            ip = et_ip.getText().toString();
            port = Integer.parseInt(et_port.getText().toString());
            uname = et_username.getText().toString();

            ServerSocket serverSocket = new ServerSocket(port, 1, InetAddress.getByName(ip));

            ServerAsync serverAsync = new ServerAsync(this);
            serverAsync.execute(serverSocket);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR : Enter correct port", Toast.LENGTH_SHORT).show();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR : IP : " + ip + " is not valid", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "ERROR : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private class ClientAsync extends AsyncTask<Void, Void, Socket> {

        private Context context;
        private ProgressDialog pd;

        private String ip = "";
        private int port = 0;
        private String uname = "";

        public ClientAsync(Context context, String ip, int port, String username) {
            this.context = context;
            this.ip = ip;
            this.port = port;
            this.uname = username;

            pd = new ProgressDialog(context);
            pd.setTitle("Connecting to " + ip);
            pd.setMessage("Please Wait");
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected void onPostExecute(Socket socket) {
            super.onPostExecute(socket);
            pd.dismiss();
            if (socket == null) {
                Toast.makeText(context, "ERROR : Connection Error", Toast.LENGTH_SHORT).show();
                return;
            }
            startChat(socket);
        }

        @Override
        protected Socket doInBackground(Void... voids) {
            try {
                Socket socket = new Socket(ip, port);
                return socket;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Toast.makeText(context, "ERROR : IP : " + ip + " is not valid", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "ERROR : " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
            return null;
        }
    }

    private class ServerAsync extends AsyncTask<ServerSocket, Void, Socket> {
        private Context context;
        private ProgressDialog pd;

        public ServerAsync(Context context) {
            this.context = context;
            pd = new ProgressDialog(context);
            pd.setTitle("Waiting for client");
            pd.setMessage("Ask client to connect to your server");
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected void onPostExecute(Socket socket) {
            super.onPostExecute(socket);
            pd.dismiss();
            if (socket == null) {
                Toast.makeText(context, "ERROR : Connection Error", Toast.LENGTH_SHORT).show();
                return;
            }
            startChat(socket);
        }

        @Override
        protected Socket doInBackground(ServerSocket... serverSockets) {
            try {
                Socket socket = serverSockets[0].accept();
                return socket;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "ERROR : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    private void startChat(Socket socket) {
        SocketHandler.setSocket(socket);
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
        finish();
    }

}
