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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import me.gosimple.nbvcxz.*;
import me.gosimple.nbvcxz.scoring.Result;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION_REQUEST_STORAGE = 1;

    private EditText password;
    private Button helpButton;
    private Button compareButton;
    private Button estimateButton;
    private Button learnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        password = (EditText) findViewById(R.id.password) ;
        helpButton = (Button) findViewById(R.id.help);
        compareButton = (Button) findViewById(R.id.compare_password);
        estimateButton = (Button) findViewById(R.id.estimate_password);
        learnButton = (Button) findViewById(R.id.learn_password);

    }

    public void estimatePasswordPressed(View view)
    {
        Log.d("MainActivity","Pressed estimate password");
        Intent intent = new Intent(this, EstimatorActivity.class);
        intent.putExtra("password",password.getText().toString());
        startActivity(intent);
    }
    public void comparePasswordPressed(View view)
    {
        Log.d("MainActivity","Pressed compare password");
        Intent intent = new Intent(this, WordListActivity.class);
        intent.putExtra("password",password.getText().toString());
        startActivity(intent);
    }
    public void crackPasswordPressed(View view)
    {
        Log.d("MainActivity","Pressed crack");
        Intent intent = new Intent(this, CrackActivity.class);
        intent.putExtra("password",password.getText().toString());
        startActivity(intent);
    }
    public void learnPasswordPressed(View view)
    {
        Log.d("MainActivity","Pressed learn");
        Intent intent = new Intent(this, LearnStuffActivity.class);
        startActivity(intent);
    }
    public void helpPressed(View view)
    {
        //TODO show help dialog?
        Toast.makeText(getApplicationContext(),"No help for you", Toast.LENGTH_LONG).show();
    }

    public void checkPasswordPressed(View view)
    {
        Intent serviceIntent = new Intent(this, QueryPassSecurityService.class);
     //   serviceIntent.putExtra("password", password.getText().toString());
        startService(serviceIntent);

    }

}
