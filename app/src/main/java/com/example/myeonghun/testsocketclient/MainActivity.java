package com.example.myeonghun.testsocketclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private String html = "";
    private Handler mHandler;

    private Socket socket;

    private BufferedReader networkReader;
    private BufferedWriter networkWriter;

    private String ipAddress = "192.168.150.11"; // IP
    private int portNum = 5005; // PORT번호

    private TextView txtResponse;
    private EditText edtTextAddress, edtTextPort, edtTextSendMsg;
    private Button btnConnect, btnClear, btnSend;

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if(socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();

        edtTextAddress = (EditText) findViewById(R.id.address);
        edtTextPort = (EditText) findViewById(R.id.port);
        btnConnect = (Button) findViewById(R.id.connect);
        btnClear = (Button) findViewById(R.id.clear);

        edtTextSendMsg = (EditText) findViewById(R.id.input);
        btnSend = (Button) findViewById(R.id.send);
        txtResponse = (TextView) findViewById(R.id.response);

        btnConnect.setOnClickListener(buttonConnectOnClickListener);
        btnClear.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                txtResponse.setText("");
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (edtTextSendMsg.getText().toString() != null || !edtTextSendMsg.getText().toString().equals("")) {
                    PrintWriter out = new PrintWriter(networkWriter, true);
                    String return_msg = edtTextSendMsg.getText().toString();
                    out.println(return_msg);
                }
            }
        });
    }

    View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {

        public void onClick(View arg0) {

            ipAddress = edtTextAddress.getText().toString();
            portNum = Integer.parseInt(edtTextPort.getText().toString());

            MainThread thread = new MainThread();
            thread.setDaemon(true);
            thread.start();

            checkUpdate.start();
        }
    };

    private Thread checkUpdate = new Thread() {

        public void run() {
            try {
                String line;
                Log.w("ChattingStart", "Start Thread");
                while (true) {
                    Log.w("Chatting is running", "chatting is running");
                    line = networkReader.readLine();
                    html = line;
                    mHandler.post(showUpdate);
                }
            } catch (Exception e) {

            }
        }
    };

    private Runnable showUpdate = new Runnable() {

        public void run() {
            Toast.makeText(MainActivity.this, "Coming word: " + html, Toast.LENGTH_SHORT).show();
            txtResponse.setText(html);
        }

    };

    public void setSocket(String ip, int port) throws IOException {

        try {
            socket = new Socket(ip, port);
            networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            networkReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }

    }

    class MainThread extends Thread {
        public void run() {
            try {
                setSocket(ipAddress, portNum);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
