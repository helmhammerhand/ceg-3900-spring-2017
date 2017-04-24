package com.example.frodo.passwordsecure;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LearnStuffActivity extends AppCompatActivity {

    TextView google;
    TextView schneier;
    TextView cmu;
    TextView arstechnica;
    TextView pass1;
    TextView pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_stuff);

        google = (TextView) findViewById(R.id.google);
        google.setMovementMethod(LinkMovementMethod.getInstance());

        schneier = (TextView) findViewById(R.id.schneier);
        schneier.setMovementMethod(LinkMovementMethod.getInstance());

        cmu = (TextView) findViewById(R.id.cmu);
        cmu.setMovementMethod(LinkMovementMethod.getInstance());

        arstechnica = (TextView) findViewById(R.id.arstechnica);
        arstechnica.setMovementMethod(LinkMovementMethod.getInstance());

        pass1 = (TextView) findViewById(R.id.pass1);
        pass1.setMovementMethod(LinkMovementMethod.getInstance());

        pass2 = (TextView) findViewById(R.id.pass2);
        pass2.setMovementMethod(LinkMovementMethod.getInstance());

    }
    public void backPressed(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
