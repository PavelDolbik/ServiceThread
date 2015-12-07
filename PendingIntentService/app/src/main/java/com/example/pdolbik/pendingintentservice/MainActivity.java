package com.example.pdolbik.pendingintentservice;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Constants {

    private Button   button;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        button   = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PendingIntent pendingIntent = createPendingResult(1, new Intent(), 0);
                Intent intent = new Intent(MainActivity.this, MyService.class);
                intent.putExtra(Constants.VALUE, 10);
                intent.putExtra(Constants.PENDING, pendingIntent);

                startService(intent);
            }
        });
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
