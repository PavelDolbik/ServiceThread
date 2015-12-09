## HandlerUIThread

Send result in UI-thread using handler method.

#### Create Executor
```java
ExecutorService executor = Executors.newSingleThreadExecutor();
```

#### Create Handler
Handler will be receive result
```java
Handler uiHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        textView.setText((String)msg.obj);
    }
};
```

#### Start do long task
You can use execute or submit.
The difference is that execute doesn't return a Future, so you can't wait for the completion of the Runnable and get any exception it throws using that.

And send result in Handler
```java
executor.submit(new Runnable() {
    @Override
    public void run() {
		String result = doLongTask();
		uiHandler.sendMessage(Message.obtain(null, 0, 0, 0, result));
	}
}
```

#### Stop executor
```java
executor.shutdown();
```

#### Force stop executor
```java
private void forceStopExecutor() {
    try {
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        executor.shutdownNow();
    }
}
```