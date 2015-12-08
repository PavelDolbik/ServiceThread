package com.example.pdolbik.paralleltaskthread;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {

    private Button   button;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleExecutor simpleExecutor = new SimpleExecutor();
                simpleExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        List<Callable<String>> tasks = new ArrayList<Callable<String>>();

                        tasks.add(new Callable<String>() {
                            @Override
                            public String call() throws Exception {
                                return getFirstPartialDataFromNetwork();
                            }
                        });

                        tasks.add(new Callable<String>() {
                            @Override
                            public String call() throws Exception {
                                return getSecondPartialDataFromNetwork();
                            }
                        });

                        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
                        try {
                            List<Future<String>> futures = executor.invokeAll(tasks);

                            final String mashedData = mashupResult(futures);

                            status.post(new Runnable() {
                                @Override
                                public void run() {
                                    status.setText(mashedData);
                                }
                            });

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        executor.shutdown();
                    }
                });
            }
        });
    }

    private String getFirstPartialDataFromNetwork() {
        SystemClock.sleep(10000);
        return "First task";
    }

    private String getSecondPartialDataFromNetwork() {
        SystemClock.sleep(2000);
        return " Second task";
    }

    private String mashupResult(List<Future<String>> futures) throws ExecutionException, InterruptedException {
        StringBuilder builder = new StringBuilder();
        for (Future<String> future : futures) {
            builder.append(future.get());
        }
        return builder.toString();
    }
}
