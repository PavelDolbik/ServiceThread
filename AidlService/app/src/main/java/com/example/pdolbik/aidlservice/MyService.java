package com.example.pdolbik.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Created by p.dolbik on 07.12.2015.
 */
public class MyService extends Service {

    IAsyncInterface.Stub mBinder = new IAsyncInterface.Stub() {
        @Override
        public void doLongTask(final int startValue, final IAsyncCallbacklInterface callback) throws RemoteException {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int i = startValue; i <= startValue+5; i++) {
                        try {
                            callback.handleResult(i);
                            TimeUnit.SECONDS.sleep(1);
                        }  catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
