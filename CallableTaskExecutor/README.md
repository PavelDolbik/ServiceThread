## CallableTaskExecutor

Executor with Callable and result from Future

#### Create separate Thread class
```java
public class SimpleExecutor implements Executor {
    @Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
```

#### Create Executor
```java
SimpleExecutor simpleExecutor = new SimpleExecutor();
```

#### Start thread
```java
simpleExecutor.execute(new Runnable() {
    @Override
    public void run() { }
};
```

#### In this thread start different thread
You can write:
- ExecutorService executor = Executors.newFixedThreadPool(1);
- ExecutorService executor = Executors.newSingleThreadExecutor();
```java
simpleExecutor.execute(new Runnable() {
    @Override
    public void run() { 
		ExecutorService executor = Executors.newFixedThreadPool(1);
	}
};
```

#### Start do long task
```java
simpleExecutor.execute(new Runnable() {
    @Override
    public void run() { 
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<String> future = executor.submit(doLongTask()); 
	}
};
```

#### Get result
```java
simpleExecutor.execute(new Runnable() {
    @Override
    public void run() { 
		ExecutorService executor = Executors.newFixedThreadPool(1);
		Future<String> future = executor.submit(doLongTask());
		//This is method blocks the thread
		String result = future.get(); 
	}
};
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
