## ParallelTaskThread

Concurrency execution of several independent tasks **(but general result)**

#### Create separate thread
```java
public class SimpleExecutor implements Executor {
	@Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
```

#### Start thread
```java
SimpleExecutor simpleExecutor = new SimpleExecutor();
simpleExecutor.execute(new Runnable() {
    @Override
    public void run() {}
});
```

#### Add long tasks
```java
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
    }
});

private String getFirstPartialDataFromNetwork() {
    SystemClock.sleep(10000);
    return "First task";
}

private String getSecondPartialDataFromNetwork() {
    SystemClock.sleep(2000);
    return " Second task";
}
```

#### Create executor and start do long tasks
```java
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                        
    }
});
```

#### Get result
```java
ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
try {
    List<Future<String>> futures = executor.invokeAll(tasks);
	final String mashedData = mashupResult(futures);

} catch (InterruptedException e) {
    e.printStackTrace();
} catch (ExecutionException e) {
    e.printStackTrace();
}

//Get result
private String mashupResult(List<Future<String>> futures) throws ExecutionException, InterruptedException {
    StringBuilder builder = new StringBuilder();
    for (Future<String> future : futures) {
        builder.append(future.get());
    }
return builder.toString();
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