## Messenger

Send data between Activity and Service using Messenger **(differents processes)**

#### Create Service
```java
public class MyService extends Service { 
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
```

#### Add Service in Manifest (other process)
```java
<service android:name=".MyService"
         android:process=":remote"/>
```

#### Create Binder
```java
public class MyService extends Service {

	private Messenger messenger;

   @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        synchronized (this) {
            while (messenger == null) {
                try {
                    wait();
                } catch (InterruptedException e) {}
            }
        }
        return messenger.getBinder();
    }
}
```

#### Create thread and add Handler
```java
private class MyThread extends Thread {
    Handler mWorkerHandler;

    @Override
    public void run() {
        Looper.prepare();
        mWorkerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
			}
        };
 
		Looper.loop();
	}
}
``` 

#### Start thread
```java
	@Override
    public void onCreate() {
        super.onCreate();
        myThread = new MyThread();
        myThread.start();
    }
```

#### Get messanger
```java
private class MyThread extends Thread {
    Handler mWorkerHandler;

    @Override
    public void run() {
        Looper.prepare();
        mWorkerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
			}
        };
 
		onWorkerPrepared();
		Looper.loop();
	}
}

private void onWorkerPrepared() {
    messenger = new Messenger(myThread.mWorkerHandler);
    synchronized(this) {
        notifyAll();
    }
}
```

#### Receive data from Activity and start long task
```java
private class MyThread extends Thread {
    Handler mWorkerHandler;

    @Override
    public void run() {
        Looper.prepare();
        mWorkerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
			 //Receive data from Activity
			 int startValue = msg.what;

				//Start long task
                for (int i = startValue; i <= startValue+5; i++) {
                    try {
                        Message message = Message.obtain(null, i);
						//Send result in Activity
                        msg.replyTo.send(message);
                        TimeUnit.SECONDS.sleep(1);
                    }  catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
			}
        };
 
		onWorkerPrepared();
		Looper.loop();
	}
}

private void onWorkerPrepared() {
    messenger = new Messenger(myThread.mWorkerHandler);
    synchronized(this) {
        notifyAll();
    }
}
```

#### Stop thread
```java
@Override
public void onDestroy() {
    super.onDestroy();
    myThread.quit();
}

public void quit() {
    mWorkerHandler.getLooper().quit();
}
```

#### Add ServiceConnection in Activity and get messanger from Server
```java
public class MainActivity extends AppCompatActivity  { 

	private ServiceConnection serviceConnection;
    private Messenger         serverMessenger;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
		
		ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                serverMessenger = new Messenger(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serverMessenger = null;
            }
        };
	}
}
```

#### Create client messanger
```java
public class MainActivity extends AppCompatActivity  { 

	private ServiceConnection serviceConnection;
    private Messenger         serverMessenger;
	private Messenger         clientMessenger;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
		
		clientMessenger = new Messenger(new IncomingHandler());
		
		ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                serverMessenger = new Messenger(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                serverMessenger = null;
            }
        };
	}
	
	private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //Receive answer from server
            String text = getResources().getString(R.string.result);
            String result = text+" "+String.valueOf(msg.what);
            textView.setText(result);
            super.handleMessage(msg);
        }
    }
}
```

#### Start Service
```java
Intent intent = new Intent(MainActivity.this, MyService.class);
bindService(intent, serviceConnection, BIND_AUTO_CREATE);
```

#### Send data to Server
```java
//Send to server value 10
Message msg = Message.obtain(null, 10);
msg.replyTo = clientMessenger;
serverMessenger.send(msg);
```

#### Unbind Service
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    if (isBound) {
        unbindService(serviceConnection);
        isBound = false;
    }
}
```