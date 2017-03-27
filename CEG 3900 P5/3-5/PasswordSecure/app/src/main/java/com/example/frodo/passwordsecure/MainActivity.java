package com.example.frodo.passwordsecure;

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

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST_STORAGE = 1;

    private EditText password;
    private TextView outputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		//the UI consists of a 
        password = (EditText) findViewById(R.id.password);
        outputView = (TextView) findViewById(R.id.output_view);

		//receiver to listen for callbacks from QueryPassSecurityService
        getApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //update text view
                outputView.setText(intent.getStringExtra("output"));
            }
        }, new IntentFilter(QueryPassSecurityService.PASSWORD_CHECK_FINISHED));


    }

	/**
	* Called when the 'Check Password' button is pressed. Ensures the app has
	* internet permission and then starts QueryPassSecurityService by calling
	* startPasswordService. Requests internet permission if the app does not
	* have it yet.
	*/
    public void checkPasswordPressed(View view)
    {
        Log.d("MainActivity","Button Pressed");
        outputView.setText("Wait...");
        //check permissions
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
			//request permission
            Log.d("MainActivity","Need permission to access internet");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSION_REQUEST_STORAGE);
        }
        else
        {
			//start service
            startPasswordService();
        }

    }

	/**
	* Called by the android system after it has finished processing a permission request.
	* Invokes the password service upon success since the permission request is initiated
	* by the user perssing the 'Check Password' button.
	*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == MY_PERMISSION_REQUEST_STORAGE)
        {
            startPasswordService();
        }
    }

	/**
	* Creates an intent containing the password contained in the text entry field
	* and sends the intent to QueryPassSecurityService. 
	*/
    private void startPasswordService()
    {
        Intent serviceIntent = new Intent(this, QueryPassSecurityService.class);
        serviceIntent.putExtra("password", password.getText().toString());
        startService(serviceIntent);
    }

}
