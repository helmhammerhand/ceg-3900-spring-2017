package edu.wright.ceg3900.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.wright.ceg3900.R;

import edu.wright.ceg3900.utils.AndroidUtils;

/**
 * Created by Paul Fuchs on 2/9/17.
 *
 * Provides a welcome screen which will start SudokuPlayActivity upon a button press
 */

public class SudokuStartActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidUtils.setThemeFromPreferences(this);

        setContentView(R.layout.sudoku_start);

        Button playGme = (Button) findViewById(R.id.button_play);

        playGme.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        //TODO handle authentication?

        Intent intent = new Intent(this, SudokuPlayActivity.class);
        startActivity(intent);
    }
}
