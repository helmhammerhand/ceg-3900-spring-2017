package com.example.frodo.passwordsecure;

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

import me.gosimple.nbvcxz.scoring.Result;

public class CrackActivity extends AppCompatActivity {

    BroadcastReceiver receiver;

    TextView outputView;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crack);

        outputView = (TextView) findViewById(R.id.output_view);
        password = (EditText) findViewById(R.id.password);

        if(getIntent().hasExtra("password"))
            password.setText(getIntent().getStringExtra("password"));

        receiver =
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        //TODO update text view

                        outputView.setText(intent.getStringExtra("output"));
                    }
                };
        getApplicationContext().registerReceiver(receiver,
                new IntentFilter(QueryPassSecurityService.PASSWORD_CRACK_FINISHED));

    }


    @Override
    protected void onDestroy() {
        getApplicationContext().unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void crackPasswordPressed(View view)
    {
        Log.d("MainActivity","Button Pressed");
        outputView.setText("Wait...");
        //check permissions
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            Log.d("MainActivity","Need permission to access internet");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    WordListActivity.MY_PERMISSION_REQUEST_STORAGE);
        }
        else
        {
            startCommonWordsService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == WordListActivity.MY_PERMISSION_REQUEST_STORAGE)
        {
            startCommonWordsService();
        }
    }
    public void backPressed(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startCommonWordsService()
    {
        Intent serviceIntent = new Intent(this, QueryPassSecurityService.class);
        serviceIntent.putExtra("type", QueryPassSecurityService.TYPE_CRACK);
        serviceIntent.putExtra("password", password.getText().toString());
        startService(serviceIntent);
    }
}
