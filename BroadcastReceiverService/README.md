## BroadcastReceiver

Send data between Activity and Service using BroadcastReceiver

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
				//Get data from Activity
				int startValue = intent.getIntExtra(Constants.VALUE, 0);

				Intent resultIntent = new Intent(Constants.BROADCAST_ACTION);
				for (int i = startValue; i <= startValue+5; i++) {
					try {
						resultIntent.putExtra(Constants.RESULT, i);
						//Send result in Activity
						sendBroadcast(resultIntent);
						TimeUnit.SECONDS.sleep(1);
					}  catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				//Stop thread
				stopSelf();
			}
		}).start();
	}
}
```

#### Registering receiver in Activity
```java
public class MainActivity extends AppCompatActivity implements Constants {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		BroadcastReceiver receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int result = intent.getIntExtra(Constants.RESULT, 0);
				textView.setText(getResources().getString(R.string.result)+" "+result);
			}
		};

		IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
		registerReceiver(receiver, intentFilter);
	}
}
```

#### Unregister receiver
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(receiver);
}
```

