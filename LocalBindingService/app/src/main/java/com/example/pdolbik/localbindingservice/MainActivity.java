package com.example.pdolbik.localbindingservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity  {

    private Button            button;
    private TextView          textView;

    private ServiceConnection serviceConnection;
    private MyService         myService;
    private boolean           isBound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        button   = (Button) findViewById(R.id.button);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                myService = ((MyService.ServiceBinder)iBinder).getService();
                //You can call public method
                //myService.publishMethod();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                myService = null;
            }
        };

        Intent intent = new Intent(MainActivity.this, MyService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        isBound = true;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myService != null) {
                    myService.doLongOperation(10, new ServiceListener(MainActivity.this));
                }
            }
        });
    }


    private static class ServiceListener implements MyService.OperationListener {

        private WeakReference<MainActivity> mWeakActivity;

        public ServiceListener(MainActivity activity) {
            this.mWeakActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void onOperationDone(final int i) {
            final MainActivity localActivity = mWeakActivity.get();
            if (localActivity != null) {
                localActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = localActivity.getResources().getString(R.string.result);
                        String result = text+" "+String.valueOf(i);
                        localActivity.textView.setText(result);
                    }
                });
            }
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
