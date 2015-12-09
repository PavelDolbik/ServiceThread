package com.example.pdolbik.threadpool;

import android.os.SystemClock;
import android.util.Log;

/**
 * Created by p.dolbik on 09.12.2015.
 */
public class MyTask implements Runnable {

    private String command;

    public MyTask(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        Log.d("Pasha", "Work "+command);
        SystemClock.sleep(7000);
    }
}
