package com.example.keynes.rollcall;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.keynes.rollcall.nsd.ChatConnection;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StudentActionActivity extends AppCompatActivity {

    private static final String ROLL_CALL = "S01";

    private Handler mUpdateHandler;

    private ChatConnection mConnection;

    private String mHost;
    private int mPort;

    private Button mCallBtn;
    private Button mQueryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_action);

        mUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String chatLine = msg.getData().getString("msg");
            }
        };

        mCallBtn = (Button)findViewById(R.id.button_call);
        mQueryBtn = (Button)findViewById(R.id.button_query);

        Intent intent = getIntent();

        mHost = intent.getStringExtra("ip");
        mPort = intent.getIntExtra("port", 0);

        Toast.makeText(this, mHost + ":" + String.valueOf(mPort),
                Toast.LENGTH_LONG).show();

        mCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
                String currDateTime = sdf.format(c.getTime());

                mConnection.sendMessage("S01B0242081" + currDateTime);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mConnection = new ChatConnection(mUpdateHandler);

        try {
            Log.e("Action", "host: " + mHost + ", port: " + mPort);
            mConnection.connectToServer(InetAddress.getByName(mHost), mPort);
        }catch (Exception e) {
            Log.e("Action", "exception");
        }
    }
}
