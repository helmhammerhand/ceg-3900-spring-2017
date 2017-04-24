package com.example.frodo.cloudcat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST_INTERNET = 1;

    private EditText hashURL;
    private EditText wordListURL;
    private TextView outputView;
    private EditText host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hashURL = (EditText) findViewById(R.id.hashurl);
        wordListURL = (EditText) findViewById(R.id.wordlisturl);
        outputView = (TextView) findViewById(R.id.output_view);
        host = (EditText) findViewById(R.id.hostname);

        getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                outputView.setText(intent.getStringExtra("output"));
            }
        }, new IntentFilter("edu.wright.ceg3900.LOG_CHECK_FINISHED"));

    }

    public void crackHashPressed(View view)
    {
        //check permissions
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("LogCheckService","Need permission to read external storage");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSION_REQUEST_INTERNET);
        }
        else
        {
            startCommonWordsService();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == MY_PERMISSION_REQUEST_INTERNET)
        {
            startCommonWordsService();
        }
    }

    private void startCommonWordsService()
    {
        Intent serviceIntent = new Intent(this, QueryHashcatService.class);
        serviceIntent.putExtra("hashurl", hashURL.getText().toString());
        serviceIntent.putExtra("wordlisturl", wordListURL.getText().toString());
        serviceIntent.putExtra("host", host.getText().toString());
        Log.d("MainActivity","Starting service");
        startService(serviceIntent);
    }

}
