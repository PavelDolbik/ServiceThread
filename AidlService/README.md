## AIDL

Send data between Activity and Service using AIDL **(differents processes)**

#### Create Aidl interface callback
```java
interface IAsyncCallbacklInterface {
    void handleResult(int i);
}
```

#### Create Aidl interface
```java
interface IAsyncInterface {
    //Async method
    oneway void doLongTask(int i, IAsyncCallbacklInterface callback);
}
```

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

    IAsyncInterface.Stub mBinder = new IAsyncInterface.Stub() {
        @Override
        public void doLongTask(final int startValue, final IAsyncCallbacklInterface callback) throws RemoteException {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    for (int i = startValue; i <= startValue+5; i++) {
                        try {
                            callback.handleResult(i);
                            TimeUnit.SECONDS.sleep(1);
                        }  catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
```

#### Add ServiceConnection in Activity and create Handler in UI-Thread
```java
public class MainActivity extends AppCompatActivity  { 

	private ServiceConnection serviceConnection;
    private IAsyncInterface   iAsyncInterface;
	private Handler           handler;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
		
		handler  = new Handler();
		
		ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                iAsyncInterface = IAsyncInterface.Stub.asInterface(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                iAsyncInterface = null;
            }
        };
	}
}
```

#### Add callback (receive result from Service)
```java
public class MainActivity extends AppCompatActivity  { 

	private ServiceConnection serviceConnection;
    private IAsyncInterface   iAsyncInterface;
	private Handler           handler;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
		
		handler  = new Handler();
		
		ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                iAsyncInterface = IAsyncInterface.Stub.asInterface(iBinder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                iAsyncInterface = null;
            }
        };
	}
	
	 private IAsyncCallbacklInterface.Stub mCallback = new IAsyncCallbacklInterface.Stub(){
        @Override
        public void handleResult(final int i) throws RemoteException {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    String text   = getResources().getString(R.string.result);
                    String result = text+" "+String.valueOf(i);
                    textView.setText(result);

                }
            });
        }
    };
}
```

#### Start Service and long task
```java
Intent intent = new Intent(MainActivity.this, MyService.class);
bindService(intent, serviceConnection, BIND_AUTO_CREATE);
iAsyncInterface.doLongTask(10, mCallback);
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