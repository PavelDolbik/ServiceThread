## RunOnUIThread

Send result in UI-thread using runOnUiThread method.

#### Create Executor
```java
ExecutorService executor = Executors.newSingleThreadExecutor();
```

#### Start do long task
You can use execute or submit.
The difference is that execute doesn't return a Future, so you can't wait for the completion of the Runnable and get any exception it throws using that.
```java
executor.submit(new Runnable() {
    @Override
    public void run() {
		doLongTask();
	}
}
```

#### Send result in UI-Thread
```java
 MainActivity.this.runOnUiThread(new Runnable() {
    @Override
    public void run() {
        textView.setText(result);
        }
    });
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
