package com.example.pdolbik.aidlservice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button            button;
    private TextView          textView;

    private ServiceConnection serviceConnection;
    private IAsyncInterface   iAsyncInterface;
    private Handler           handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                iAsyncInterface = IAsyncInterface.Stub.asInterface(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                iAsyncInterface = null;
            }
        };


        Intent intent = new Intent(MainActivity.this, MyService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);


        handler  = new Handler();
        textView = (TextView) findViewById(R.id.textView);
        button   = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iAsyncInterface != null) {
                    try {
                        iAsyncInterface.doLongTask(10, mCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private IAsyncCallbacklInterface.Stub mCallback = new IAsyncCallbacklInterface.Stub(){
        @Override
        public void handleResult(final int i) throws RemoteException {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    String text   = getResources().getString(R.string.result);
                    String result = text+" "+String.valueOf(i);
                    textView.setText(result);

                }
            });
        }
    };
}
