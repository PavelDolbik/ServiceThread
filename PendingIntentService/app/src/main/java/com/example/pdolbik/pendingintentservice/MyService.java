package com.example.pdolbik.pendingintentservice;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Created by p.dolbik on 07.12.2015.
 */
public class MyService extends Service implements Constants {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doLongTask(intent);
        return super.onStartCommand(intent, flags, startId);
    }


    private void doLongTask(final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int startValue = intent.getIntExtra(Constants.VALUE, 0);
                PendingIntent pendingIntent = intent.getParcelableExtra(Constants.PENDING);

                Intent resultIntent = new Intent();
                for (int i = startValue; i <= startValue+5; i++) {
                    try {
                        resultIntent.putExtra(Constants.RESULT, i);
                        pendingIntent.send(MyService.this, 1, resultIntent);
                        TimeUnit.SECONDS.sleep(1);
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                stopSelf();
            }
        }).start();
    }
}
