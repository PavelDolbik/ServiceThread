package com.example.pdolbik.runonuithread;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button          button;
    private TextView        textView;
    private ExecutorService executor;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        button   = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                executor = Executors.newSingleThreadExecutor();
                executor.submit(new Runnable() {
                    @Override
                    public void run() {

                        final String result = doLongTask();

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(result);
                            }
                        });

                        stopExecutor();
                    }
                });
            }
        });
    }


    private String doLongTask() {
        SystemClock.sleep(1000);
        return "Task finished work";
    }



    private void stopExecutor() {
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }

}
