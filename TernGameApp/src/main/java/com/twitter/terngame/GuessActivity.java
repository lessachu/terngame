package com.twitter.terngame;

import android.app.Activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class GuessActivity extends Activity {

    private TextView mGuessText;
    private TextView mResponseText;
    private TextView mDuplicateText;

    private String mGuess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_activity);

        mGuessText = (TextView) findViewById(R.id.guess_word_text);
        mResponseText = (TextView) findViewById(R.id.guess_result_text);
        mDuplicateText = (TextView) findViewById(R.id.duplicate_text);

        Bundle extras = getIntent().getExtras();
        String value = null;
        String response = null;
        String duplicate = null;
        if(extras != null) {
            value = extras.getString("guess_phrase");
            response = extras.getString("response_phrase");
            duplicate = extras.getString("duplicate_phrase");
        }

        if(value != null) {
            mGuessText.setText(value);
            mResponseText.setText(response);

            if(duplicate != null) {
                mDuplicateText.setText(duplicate);
            }
        }

        // hide the tweet box unless it's a successful guess

    }

    
}
