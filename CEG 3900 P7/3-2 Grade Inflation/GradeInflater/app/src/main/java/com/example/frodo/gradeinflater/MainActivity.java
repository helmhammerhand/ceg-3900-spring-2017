package com.example.frodo.gradeinflater;

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

    private EditText googleID;
    private EditText host;
    private TextView outputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleID = (EditText) findViewById(R.id.driveurl);
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
            startAveragerService();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == MY_PERMISSION_REQUEST_INTERNET)
        {
            startAveragerService();
        }
    }

    private void startAveragerService()
    {
        Intent serviceIntent = new Intent(this, QueryAveragerService.class);
        serviceIntent.putExtra("driveurl", "https://docs.google.com/uc?export=download&amp;confirm=QBiP&amp;&id="+googleID.getText().toString());
        serviceIntent.putExtra("host", host.getText().toString());
        Log.d("MainActivity","Starting service");
        startService(serviceIntent);
    }

}
