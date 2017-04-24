package com.example.frodo.passwordsecure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.scoring.Result;

public class EstimatorActivity extends AppCompatActivity {

    private EditText password;
    private TextView output;
    private Nbvcxz passRater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimator);

        passRater = new Nbvcxz();

        password = (EditText) findViewById(R.id.password);
        output = (TextView) findViewById(R.id.output_view);
        output.setText("Password Strength Feedback");

        if(getIntent().hasExtra("password"))
            password.setText(getIntent().getStringExtra("password"));
    }


    public void checkPasswordPressed(View view)
    {

        Result result = passRater.estimate(password.getText().toString());

        String feedback = "";
        feedback += "Entropy: "+Double.toString(result.getEntropy())+"\n\n";
        if(result.getFeedback().getWarning() != null)
            feedback += "Warnings: "+ result.getFeedback().getWarning()+"\n\n";
        if(result.getFeedback().getSuggestion() != null)
            feedback += "Suggestions: "+result.getFeedback().getSuggestion().toString();

        output.setText(feedback);
    }

    public void backPressed(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
