package com.example.pdolbik.serialtaskthread;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int FIRST_TASK  = 1;
    private static final int SECOND_TASK = 2;

    private Button   button;
    private TextView firstTask;
    private TextView secondTask;

    private MyThread myThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myThread = new MyThread();
        myThread.start();

        button     = (Button) findViewById(R.id.button);
        firstTask  = (TextView) findViewById(R.id.textView);
        secondTask = (TextView) findViewById(R.id.textView2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myThread.doLongTask();
            }
        });
    }


    Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FIRST_TASK:
                    firstTask.setText((String)msg.obj);
                    break;
                case SECOND_TASK:
                    secondTask.setText((String)msg.obj);
                    break;
            }
        }
    };

    private class MyThread extends HandlerThread {
        private Handler threadHandler;

        public MyThread() {
            super("MyThread", Process.THREAD_PRIORITY_BACKGROUND);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            threadHandler = new Handler(getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case FIRST_TASK:
                            String result = firstNetworkTask();
                            if (result != null) {
                                uiHandler.sendMessage(Message.obtain(null, FIRST_TASK, 0, 0, result));
                                sendMessage(obtainMessage(SECOND_TASK, result));
                            } else {
                                uiHandler.sendMessage(Message.obtain(null, FIRST_TASK, 0, 0, "First task error"));
                            }
                            break;
                        case SECOND_TASK:
                            String resultSecond = secondNetworkTask((String)msg.obj);
                            if (resultSecond != null) {
                                uiHandler.sendMessage(Message.obtain(null, SECOND_TASK, 0, 0, resultSecond));
                            } else {
                                uiHandler.sendMessage(Message.obtain(null, SECOND_TASK, 0, 0, "Second task error"));
                            }
                            break;
                    }
                }
            };
        }


        private void doLongTask() {
            threadHandler.sendEmptyMessage(FIRST_TASK);
        }


        private String firstNetworkTask() {
            SystemClock.sleep(2000);
            return "First task finish work";
        }


        private String secondNetworkTask(String firstTaskResult) {
            SystemClock.sleep(2000);
            return firstTaskResult+" & Second task finish work";
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        myThread.quit();
    }
}
