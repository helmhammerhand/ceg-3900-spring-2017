package com.example.frodo.commonwordcounter;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST_STORAGE = 1;

    private EditText inputFile;
    private EditText outputFile;
    private EditText hostName;
    private EditText wordCount;
    private TextView outputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputFile = (EditText) findViewById(R.id.infile);
        outputFile = (EditText) findViewById(R.id.outfile);
        outputView = (TextView) findViewById(R.id.output_view);
        wordCount = (EditText) findViewById(R.id.count);
        hostName = (EditText) findViewById(R.id.host_name);

        getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //TODO update text view
                outputView.setText(intent.getStringExtra("output"));
            }
        }, new IntentFilter("edu.wright.ceg3900.LOG_CHECK_FINISHED"));


    }

    public void checkLogPressed(View view)
    {
        //check permissions
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("LogCheckService","Need permission to read external storage");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET},
                    MY_PERMISSION_REQUEST_STORAGE);
        }
        else
        {
            startCommonWordsService();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == MY_PERMISSION_REQUEST_STORAGE)
        {
            startCommonWordsService();
        }
    }

    private void startCommonWordsService()
    {
        Intent serviceIntent = new Intent(this, QueryCommonWordsService.class);
        serviceIntent.putExtra("urlfile", inputFile.getText().toString());
        serviceIntent.putExtra("outfile", outputFile.getText().toString());
        serviceIntent.putExtra("count", Integer.parseInt(wordCount.getText().toString()));
        serviceIntent.putExtra("host", hostName.getText().toString());
        startService(serviceIntent);
    }

}
