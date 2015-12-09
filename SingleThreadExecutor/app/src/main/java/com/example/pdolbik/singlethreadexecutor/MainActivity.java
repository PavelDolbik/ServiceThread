package com.example.pdolbik.singlethreadexecutor;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button   button;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        button   = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        doLongTask();
                        try {
                            executor.shutdown();
                            executor.awaitTermination(5, TimeUnit.SECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            executor.shutdownNow();
                        }
                    }
                });
            }
        });
    }


    private void doLongTask() {
        for (int i = 0; i <= 10; i++) {
            final int finalI = i;
            textView.post(new Runnable() {
                @Override
                public void run() { textView.setText(String.valueOf(finalI)); }
            });
            SystemClock.sleep(1000);
        }
    }

}
