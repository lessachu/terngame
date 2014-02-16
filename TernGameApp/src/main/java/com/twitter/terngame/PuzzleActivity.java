package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.terngame.data.AnswerInfo;
import com.twitter.terngame.util.AnswerChecker;

import java.util.ArrayList;

public class PuzzleActivity extends Activity
    implements View.OnClickListener{

    private EditText mAnswerEditText;
    private TextView mAnswerTitleTextView;
    private Button mAnswerButton;
    private Button mPuzzleButton;
    private TextView mStatusTextView;
    private Chronometer mPuzzleTimer;
    private String mPuzzleID;

    // TODO: this really shouldn't go here
    private static String s_SKIP = "skip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.puzzle_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPuzzleID = extras.getString("puzzleID");
        }

        Log.d("terngame", "puzzleID: " + mPuzzleID);

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
    protected void onResume() {
        super.onResume();

        // goes here because we come back to this Activity a lot
        Session s = Session.getInstance(this);
        final TextView puzzleName = (TextView) findViewById(R.id.puzzle_name_text);
        puzzleName.setText(s.getPuzzleName(mPuzzleID));

        // only enable the puzzle button if the puzzle has one
        if (s.showPuzzleButton()) {
            // put the current puzzle name in there
            mPuzzleButton.setText(s.getPuzzleButtonText());
            mPuzzleButton.setVisibility(View.VISIBLE);
        } else {
            mPuzzleButton.setVisibility(View.GONE);
        }

        mStatusTextView = (TextView) findViewById(R.id.status_text);
        if (s.puzzleSkipped(mPuzzleID)) {
            setCompletedPuzzleUI(getString(R.string.skipped_text));
        } else if (s.puzzleSolved(mPuzzleID)) {
            setCompletedPuzzleUI(getString(R.string.solved_text));
        } else {
            mStatusTextView.setVisibility(View.GONE);
            setAnswerUIVisibility(View.VISIBLE);
            mPuzzleTimer.setBase(s.getPuzzleStartTime(mPuzzleID));
            mPuzzleTimer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Session s = Session.getInstance(this);
        if (!s.puzzleSkipped(mPuzzleID) && !s.puzzleSolved(mPuzzleID)) {
            mPuzzleTimer.stop();
        }
    }

    public void setCompletedPuzzleUI(String status_text) {
        Session s = Session.getInstance(this);
        mStatusTextView.setText(status_text);
        mStatusTextView.setVisibility(View.VISIBLE);
        setAnswerUIVisibility(View.GONE);

        // set the Timer status
        long timeElapsed = s.getPuzzleEndTime(mPuzzleID) - s.getPuzzleStartTime(mPuzzleID);
        mPuzzleTimer.setBase(SystemClock.elapsedRealtime() - timeElapsed);
    }

    // hrm, should I be hiding the layout instead?
    public void setAnswerUIVisibility(int visibility) {
        mAnswerButton.setVisibility(visibility);
        mAnswerTitleTextView.setVisibility(visibility);
        mAnswerEditText.setVisibility(visibility);
    }

    public void onClick(View view) {
        final int id = view.getId();

        Session s = Session.getInstance(this);
        if (id == R.id.answer_button) {
            String guess = mAnswerEditText.getText().toString();
            if (AnswerChecker.stripAnswer(guess).equalsIgnoreCase(s_SKIP)) {
                s.skipPuzzle();
                mPuzzleTimer.stop();
                // launch into Answer Screen, but with skipped text
            } else {
                // register a guess
                AnswerInfo ai = s.guessAnswer(guess);
                Intent i = new Intent(this, GuessActivity.class);
                i.putExtra("guess_phrase", guess);
                i.putExtra("response_phrase", ai.mResponse);
                if(ai.mDuplicate) {
                    i.putExtra("duplicate_phrase", s.getDuplicateAnswerString());
                }
                i.putExtra("correctness",ai.mCorrect);

                if (ai.mCorrect) {
                    mPuzzleTimer.stop();
                }
                startActivity(i);
            }

        } else if (id == R.id.hint_button) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Yay! You hit the hint button!",
                    Toast.LENGTH_SHORT);
            toast.show();
        } else if (id == R.id.guess_log_button) {
            ArrayList<String> guesses = s.getGuesses(mPuzzleID);

            if (guesses != null) {
                Intent i = new Intent(this, GuessLogActivity.class);
                i.putExtra("guesses", guesses);
                startActivity(i);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "You haven't made any guesses yet!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }

        } else if (id == R.id.do_puzzle_button) {
            // for now show a toast
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Woot! Prepare for battle!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
