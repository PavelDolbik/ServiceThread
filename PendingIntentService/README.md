## PendingIntent

Send data between Activity and Service using PendingIntent

#### Create constants 
```java
public interface Constants {
    public final String VALUE            = "value";
    public final String RESULT           = "result";
    public final String BROADCAST_ACTION = "pavel.dolbik.action.receiver";
}
```

#### Create Service
```java
public class MyService extends Service implements Constants {

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
}
```

#### Add Service in Manifest
```java
<service android:name=".MyService"/>
```

#### Add long task in Service and send result in Activity
```java
public class MyService extends Service implements Constants {

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		doLongTask(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	private void doLongTask(final Intent intent) {
		//Start new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
				//Get value from Activity
                int startValue = intent.getIntExtra(Constants.VALUE, 0);
                PendingIntent pendingIntent = intent.getParcelableExtra(Constants.PENDING);

                Intent resultIntent = new Intent();
                for (int i = startValue; i <= startValue+5; i++) {
                    try {
                        resultIntent.putExtra(Constants.RESULT, i);
						//Send result in Activity
                        pendingIntent.send(MyService.this, 1, resultIntent);
                        TimeUnit.SECONDS.sleep(1);
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
				//Stop service
                stopSelf();
            }
        }).start();
    }
}
```

#### Start Service
```java
public class MainActivity extends AppCompatActivity implements Constants {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PendingIntent pendingIntent = createPendingResult(1, new Intent(), 0);
        Intent intent = new Intent(MainActivity.this, MyService.class);
        intent.putExtra(Constants.VALUE, 10);
        intent.putExtra(Constants.PENDING, pendingIntent);

        startService(intent);
	}
}
```

#### Get result from Service
```java
public class MainActivity extends AppCompatActivity implements Constants {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PendingIntent pendingIntent = createPendingResult(1, new Intent(), 0);
        Intent intent = new Intent(MainActivity.this, MyService.class);
        intent.putExtra(Constants.VALUE, 10);
        intent.putExtra(Constants.PENDING, pendingIntent);

        startService(intent);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            int result = data.getIntExtra(Constants.RESULT, 0);
            textView.setText(getResources().getString(R.string.result)+" "+result);
        }
    }
}
```

