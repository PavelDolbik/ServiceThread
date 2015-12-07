package com.example.pdolbik.localbindingservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Created by p.dolbik on 07.12.2015.
 */
public class MyService extends Service {

    public interface OperationListener {
        public void onOperationDone(int i);
    }


    private final ServiceBinder serviceBinder = new ServiceBinder();


    public class ServiceBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }


    public void doLongOperation(final int startValue, final OperationListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = startValue; i <= startValue+5; i++) {
                    try {
                        listener.onOperationDone(i);
                        TimeUnit.SECONDS.sleep(1);
                    }  catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }


    public void publishMethod() {
        Log.d("Pasha", "Work publishMethod");
    }

}
