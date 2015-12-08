package com.example.pdolbik.messengerservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button            button;
    private TextView          textView;

    private ServiceConnection serviceConnection;
    private Messenger         serverMessenger;
    private Messenger         clientMessenger;
    private boolean           isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientMessenger = new Messenger(new IncomingHandler());

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                serverMessenger = new Messenger(iBinder);
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serverMessenger = null;
            }
        };


        Intent intent = new Intent(MainActivity.this, MyService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        textView = (TextView) findViewById(R.id.textView);
        button   = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //Send to server value 10
                    Message msg = Message.obtain(null, 10);
                    msg.replyTo = clientMessenger;
                    serverMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //Receive answer from server
            String text = getResources().getString(R.string.result);
            String result = text+" "+String.valueOf(msg.what);
            textView.setText(result);
            super.handleMessage(msg);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
