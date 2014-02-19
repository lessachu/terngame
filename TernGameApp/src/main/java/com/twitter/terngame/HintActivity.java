package com.twitter.terngame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HintActivity extends Activity {

    // intent keys
    public static final String s_hint_title = "hint_title";
    public static final String s_puzzle_title = "puzzle_title";
    public static final String s_hint_text = "hint_text";

    private TextView mHintTitleText;
    private TextView mPuzzleTitleText;
    private TextView mHintText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hint_activity);

        mHintTitleText = (TextView) findViewById(R.id.hint_activity_label);
        mPuzzleTitleText = (TextView) findViewById(R.id.hint_puzzle_text);
        mHintText = (TextView) findViewById(R.id.hint_text);

        Bundle extras = getIntent().getExtras();
        String hintTitle = null;
        String puzzleTitle = null;
        String hintText = null;
        if (extras != null) {
            hintTitle = extras.getString(s_hint_title);
            puzzleTitle = extras.getString(s_puzzle_title);
            hintText = extras.getString(s_hint_text);
        }

        if (hintTitle != null) {
            mHintTitleText.setText(hintTitle);
        }
        if (puzzleTitle != null) {
            mPuzzleTitleText.setText(puzzleTitle);
        }
        if (hintText != null) {
            mHintText.setText(hintText);
        }
    }

}
