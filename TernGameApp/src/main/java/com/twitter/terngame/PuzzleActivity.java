package com.twitter.terngame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.twitter.terngame.data.AnswerInfo;
import com.twitter.terngame.util.AnswerChecker;

import java.util.ArrayList;

public class PuzzleActivity extends BaseActivity
        implements View.OnClickListener, Session.HintListener {

    // Intent keys
    public static final String s_puzzleID = "puzzleID";
    public static final String s_hintPrompt = "hintPrompt";

    private EditText mAnswerEditText;
    private TextView mAnswerTitleTextView;
    private Button mAnswerButton;
    private Button mPuzzleButton;
    private TextView mStatusTextView;
    private Chronometer mPuzzleTimer;
    private String mPuzzleID;
    private boolean mHintPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.puzzle_activity);

        mPuzzleButton = (Button) findViewById(R.id.do_puzzle_button);
        mPuzzleButton.setOnClickListener(this);

        final Button guessLogButton = (Button) findViewById(R.id.guess_log_button);
        guessLogButton.setOnClickListener(this);

        final Button hintButton = (Button) findViewById(R.id.hint_button);
        hintButton.setOnClickListener(this);

        mAnswerTitleTextView = (TextView) findViewById(R.id.answer_title_text);
        mPuzzleTimer = (Chronometer) findViewById(R.id.puzzle_chronometer);

        mAnswerButton = (Button) findViewById(R.id.answer_button);
        mAnswerButton.setOnClickListener(this);
        mAnswerButton.setEnabled(false);

        mAnswerEditText = (EditText) findViewById(R.id.answer_text);
        mAnswerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {
                mAnswerButton.setEnabled(text.length() > 0);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        mSession.registerHintListener(this);
    }

    @Override
    public void showUX() {
        super.showUX();
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        mHintPrompt = false;
        if (extras != null) {
            mPuzzleID = extras.getString(s_puzzleID);
            if (i.hasExtra(s_hintPrompt)) {
                mHintPrompt = extras.getBoolean(s_hintPrompt);
            }
        }

        final TextView puzzleName = (TextView) findViewById(R.id.puzzle_name_text);
        puzzleName.setText(mSession.getPuzzleName(mPuzzleID));

        if (mSession.showPuzzleButton(mPuzzleID)) {
            mPuzzleButton.setText(mSession.getPuzzleButtonText(mPuzzleID));
            mPuzzleButton.setVisibility(View.VISIBLE);
        } else {
            mPuzzleButton.setVisibility(View.GONE);
        }

        mStatusTextView = (TextView) findViewById(R.id.status_text);
        if (mSession.puzzleSkipped(mPuzzleID)) {
            setCompletedPuzzleUI(getString(R.string.skipped_text));
        } else if (mSession.puzzleSolved(mPuzzleID)) {
            setCompletedPuzzleUI(getString(R.string.solved_text));
        } else {
            if (mHintPrompt) {
                mStatusTextView.setText(getString(R.string.hint_prompt));
            } else {
                mStatusTextView.setText("");
            }
            setAnswerUIVisibility(View.VISIBLE);
            mPuzzleTimer.setBase(mSession.getPuzzleStartTime(mPuzzleID));
            mPuzzleTimer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Session s = Session.getInstance(this);
        s.unregisterHintListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mSession.puzzleSkipped(mPuzzleID) && !mSession.puzzleSolved(mPuzzleID)) {
            mPuzzleTimer.stop();
        }
    }

    public void onHintReady(String puzzleID, String hintID, int notificationID) {
        // crashes here if you hit a push notification during loading
        mStatusTextView.setText(getString(R.string.hint_prompt));
    }

    public void setCompletedPuzzleUI(String status_text) {
        mStatusTextView.setText(status_text);
        setAnswerUIVisibility(View.GONE);

        long timeElapsed = mSession.getPuzzleEndTime(mPuzzleID) - mSession.getPuzzleStartTime(mPuzzleID);
        mPuzzleTimer.setBase(SystemClock.elapsedRealtime() - timeElapsed);
    }

    public void setAnswerUIVisibility(int visibility) {
        mAnswerButton.setVisibility(visibility);
        mAnswerTitleTextView.setVisibility(visibility);
        mAnswerEditText.setVisibility(visibility);
    }

    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.answer_button) {
            String guess = mAnswerEditText.getText().toString();
            mAnswerEditText.setText("");

            if (AnswerChecker.stripAnswer(guess).equalsIgnoreCase(mSession.getSkipCode())) {
                String answer = mSession.getCorrectAnswer(mPuzzleID);
                String response = mSession.skipPuzzle(mPuzzleID);
                mPuzzleTimer.stop();

                Intent i = new Intent(this, GuessActivity.class);
                i.putExtra(GuessActivity.s_is_skip, true);
                i.putExtra(GuessActivity.s_guess_word, answer);
                i.putExtra(GuessActivity.s_response, response);
                i.putExtra(GuessActivity.s_correct, false);
                startActivity(i);

            } else {
                // register a guess
                AnswerInfo ai = mSession.guessAnswer(guess);
                Intent i = new Intent(this, GuessActivity.class);
                i.putExtra(GuessActivity.s_guess_word, guess);
                i.putExtra(GuessActivity.s_response, ai.mResponse);
                if (ai.mDuplicate) {
                    i.putExtra(GuessActivity.s_duplicate, mSession.getDuplicateAnswerString());
                }
                i.putExtra(GuessActivity.s_correct, ai.mCorrect);

                if (ai.mCorrect) {
                    mPuzzleTimer.stop();
                }
                startActivity(i);
            }

        } else if (id == R.id.hint_button) {
            // if we go to the hint list activity, clear the hint notification prompt
            //mHintPrompt = false;
            Intent i = getIntent();
            i.putExtra(this.s_hintPrompt, false);
            setIntent(i);

            i = new Intent(this, HintListActivity.class);
            i.putExtra(HintListActivity.s_puzzleID, mPuzzleID);
            startActivity(i);
        } else if (id == R.id.guess_log_button) {
            ArrayList<String> guesses = mSession.getGuesses(mPuzzleID);

            if (guesses != null) {
                Intent i = new Intent(this, GuessLogActivity.class);
                i.putExtra(GuessLogActivity.s_guess_key, guesses);
                startActivity(i);
            }
        }
    }
}
