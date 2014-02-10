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

    private TextView mGuessText;
    private TextView mResponseText;
    private TextView mDuplicateText;
    private Button mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_activity);

        mGuessText = (TextView) findViewById(R.id.guess_word_text);
        mResponseText = (TextView) findViewById(R.id.guess_result_text);
        mDuplicateText = (TextView) findViewById(R.id.duplicate_text);
        mShareButton = (Button) findViewById(R.id.share_button);

        mShareButton.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        String value = null;
        String response = null;
        String duplicate = null;
        boolean guess_correct = false;
        if(extras != null) {
            value = extras.getString("guess_phrase");
            response = extras.getString("response_phrase");
            duplicate = extras.getString("duplicate_phrase");
            guess_correct = extras.getBoolean("correctness");
        }

        if(value != null) {
            mGuessText.setText(value);
            mResponseText.setText(response);

            if(duplicate != null) {
                mDuplicateText.setText(duplicate);
            }
        }

        if (!guess_correct) {
            mShareButton.setVisibility(View.GONE);
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
