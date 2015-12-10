## ThreadPool

ThreadPool with custom Factory.

#### Create ThreadFactory
```java
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
```

#### Create long task
```java
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
```

#### Create Executor
```java
ExecutorService executor = Executors.newFixedThreadPool(5, new LowPriorityThreadFactory());
```

#### Start a lot of tasks
```java
for (int i = 0; i <= 10; i++) {
    MyTask myTask = new MyTask("Task "+i);
    executor.execute(myTask);
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