package com.example.pdolbik.messengerservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Created by p.dolbik on 07.12.2015.
 */
public class MyService extends Service {

    private MyThread  myThread;
    private Messenger messenger;

    @Override
    public void onCreate() {
        super.onCreate();
        myThread = new MyThread();
        myThread.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        synchronized (this) {
            while (messenger == null) {
                try {
                    wait();
                } catch (InterruptedException e) {}
            }
        }
        return messenger.getBinder();
    }

    private class MyThread extends Thread {
        Handler mWorkerHandler;

        @Override
        public void run() {
            Looper.prepare();
            mWorkerHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    int startValue = msg.what;

                    for (int i = startValue; i <= startValue+5; i++) {
                        try {
                            Message message = Message.obtain(null, i);
                            msg.replyTo.send(message);
                            TimeUnit.SECONDS.sleep(1);
                        }  catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                }
            };
            onWorkerPrepared();
            Looper.loop();
        }

        public void quit() {
            mWorkerHandler.getLooper().quit();
        }
    }

    private void onWorkerPrepared() {
        messenger = new Messenger(myThread.mWorkerHandler);
        synchronized(this) {
            notifyAll();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myThread.quit();
    }

}
