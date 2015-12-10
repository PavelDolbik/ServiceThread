## LocalBinding

Send data between Activity and Service using LocalBinding

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

#### Add Service in Manifest
```java
<service android:name=".MyService"/>
```

#### Create Binder
```java
public class MyService extends Service { 

	private final ServiceBinder serviceBinder = new ServiceBinder();
	
	public class ServiceBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return serviceBinder;
	}
}
```

#### Add listener in Service
```java
public class MyService extends Service { 

	public interface OperationListener {
        public void onOperationDone(int i);
    }
	
	private final ServiceBinder serviceBinder = new ServiceBinder();
	
	public class ServiceBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return serviceBinder;
	}
}
```

#### Add long operation in Service
```java
public class MyService extends Service { 

	public interface OperationListener {
        public void onOperationDone(int i);
    }
	
	private final ServiceBinder serviceBinder = new ServiceBinder();
	
	public class ServiceBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return serviceBinder;
	}
	
	public void doLongOperation(final int startValue, final OperationListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = startValue; i <= startValue+5; i++) {
                    try {
                        listener.onOperationDone(i);
                        TimeUnit.SECONDS.sleep(1);
                    }  catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
}
```

#### Add ServiceConnection in Activity
```java
public class MainActivity extends AppCompatActivity  { 
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
		
		ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                myService = ((MyService.ServiceBinder)iBinder).getService();
                //You can call public method
                //myService.publishMethod();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                myService = null;
            }
        };
	}
}
```

#### Add ServiceListener (receive result from Service)
```java
public class MainActivity extends AppCompatActivity  { 
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
		
		ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                myService = ((MyService.ServiceBinder)iBinder).getService();
                //You can call public method
                //myService.publishMethod();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                myService = null;
            }
        };
	}
	
	private static class ServiceListener implements MyService.OperationListener {

        private WeakReference<MainActivity> mWeakActivity;

        public ServiceListener(MainActivity activity) {
            this.mWeakActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void onOperationDone(final int i) {
            final MainActivity localActivity = mWeakActivity.get();
            if (localActivity != null) {
                localActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = localActivity.getResources().getString(R.string.result);
                        String result = text+" "+String.valueOf(i);
                        localActivity.textView.setText(result);
                    }
                });
            }
        }
    }
}
```

#### Start Service 
```java
Intent intent = new Intent(MainActivity.this, MyService.class);
bindService(intent, serviceConnection, BIND_AUTO_CREATE);
myService.doLongOperation(10, new ServiceListener(MainActivity.this));
```

#### Stop Service
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

