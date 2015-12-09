package com.example.pdolbik.threadpool;

import android.util.Log;

import java.util.concurrent.ThreadFactory;

/**
 * Created by p.dolbik on 09.12.2015.
 */
public class LowPriorityThreadFactory implements ThreadFactory {

    private static int count = 1;
    
    @Override
    public Thread newThread(Runnable runnable) {
        Thread t = new Thread(runnable);
        t.setName("LowPrio " + (count++));
        t.setPriority(4);
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                Log.d("Pasha", "Thread "+thread.getName()+" error "+throwable.getMessage());
            }
        });
        return t;
    }
}
