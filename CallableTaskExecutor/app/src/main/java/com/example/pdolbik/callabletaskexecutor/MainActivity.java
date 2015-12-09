package com.example.pdolbik.callabletaskexecutor;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

                final SimpleExecutor simpleExecutor = new SimpleExecutor();
                simpleExecutor.execute(new Runnable() {
                    @Override
                    public void run() {

                        executor = Executors.newFixedThreadPool(1);
                        final Future<String> future = executor.submit(doLongTask());

                        stopExecutor();

                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    textView.setText(future.get());
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
    }


    private Callable doLongTask() {
        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                SystemClock.sleep(3000);
                return "Task finish work";
            }
        };
        return task;
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
