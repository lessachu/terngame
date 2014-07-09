package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.twitter.terngame.util.ShareStatus;

public class GuessActivity extends Activity
        implements View.OnClickListener {

    // intent keys
    public static final String s_is_skip = "is_skip";
    public static final String s_guess_word = "guess_phrase";
    public static final String s_response = "response_phrase";
    public static final String s_duplicate = "duplicate_phrase";
    public static final String s_correct = "correctness";

    private TextView mGuessLabelText;
    private TextView mGuessText;
    private TextView mResponseText;
    private TextView mStatusText;
    private Button mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_activity);

        mGuessLabelText = (TextView) findViewById(R.id.guess_activity_label);
        mGuessText = (TextView) findViewById(R.id.guess_word_text);
        mResponseText = (TextView) findViewById(R.id.guess_result_text);
        mStatusText = (TextView) findViewById(R.id.status_text);
        mShareButton = (Button) findViewById(R.id.share_button);

        mShareButton.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        boolean skip = false;
        String value = null;
        String response = null;
        String duplicate = null;
        boolean guess_correct = false;
        if (extras != null) {
            skip = extras.getBoolean(s_is_skip);
            value = extras.getString(s_guess_word);
            response = extras.getString(s_response);
            duplicate = extras.getString(s_duplicate);
            guess_correct = extras.getBoolean(s_correct);
        }

        if (skip) {
            mGuessLabelText.setText(getString(R.string.skip_label));
        }

        if (value != null) {
            mGuessText.setText(value);
            mResponseText.setText(response);

            if (duplicate != null) {
                mStatusText.setText(duplicate);
            } else {
                mStatusText.setText("");
            }
        }

        if (!guess_correct) {
            mShareButton.setVisibility(View.GONE);
        } else {
            mStatusText.setText("CORRECT!");
        }
    }

    public void onClick(View view) {
        final int id = view.getId();
        Session s = Session.getInstance(this);
        if (id == R.id.share_button) {
            Intent shareIntent = ShareStatus.getGameStatusIntent(s);
            startActivity(Intent.createChooser(shareIntent,
                    this.getString(R.string.share_chooser_title)));
        }
    }
}
