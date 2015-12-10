## SerialTaskThread

Serial tasks, combining tasks into a chain (using HandlerThread)

#### Create variables
```java
private static final int FIRST_TASK  = 1;
private static final int SECOND_TASK = 2;
```
#### Create Handler in UI-thread
```java
Handler uiHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case FIRST_TASK:
				//do something with result
                firstTask.setText((String)msg.obj);
                break;
            case SECOND_TASK:
				//do something with result
                secondTask.setText((String)msg.obj);
                 break;
            }
    }
};
```

#### Create separate thread for long tasks
```java
private class MyThread extends HandlerThread {
    public MyThread() {
        super("MyThread", Process.THREAD_PRIORITY_BACKGROUND);
    }
}
```

#### Add handler in thread
```java
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
                    
            }
        };
    }
}
```

#### Add different tasks
```java
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
```

#### Send result in UI-thread
```java
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
```

#### Start thread
```java
MyThread myThread = new MyThread();
myThread.start();
myThread.doLongTask();
```

#### Stop thread
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    myThread.quit();
}
```