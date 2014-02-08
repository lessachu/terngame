package com.twitter.terngame;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.terngame.data.AnswerInfo;
import com.twitter.terngame.util.AnswerChecker;

public class PuzzleActivity extends Activity
    implements View.OnClickListener{

    private EditText mAnswerEditText;
    private Button mPuzzleButton;

    // this really shouldn't go here
    private static String s_SKIP = "skip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.puzzle_activity);

        mPuzzleButton = (Button) findViewById(R.id.do_puzzle_button);
        mPuzzleButton.setOnClickListener(this);

        final Button answerLogButton = (Button) findViewById(R.id.answer_log_button);
        answerLogButton.setOnClickListener(this);

        final Button hintButton = (Button) findViewById(R.id.hint_button);
        hintButton.setOnClickListener(this);

        final Button answerButton = (Button) findViewById(R.id.answer_button);
        answerButton.setOnClickListener(this);
        answerButton.setEnabled(false);

        mAnswerEditText = (EditText) findViewById(R.id.answer_text);
        mAnswerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {
                answerButton.setEnabled(text.length() > 0);
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
    protected void onStart(){
        super.onStart();

        // goes here because we come back to this Activity a lot
        Session s = Session.getInstance(this);
        final TextView puzzleName = (TextView) findViewById(R.id.puzzle_name_text);
        puzzleName.setText(s.getPuzzleName());

        // only enable the puzzle button if the puzzle has one
        if (s.showPuzzleButton()) {
            // put the current puzzle name in there
            mPuzzleButton.setText(s.getPuzzleButtonText());
            mPuzzleButton.setVisibility(View.VISIBLE);
        } else {
            mPuzzleButton.setVisibility(View.GONE);
        }
    }

    public void onClick(View view) {
        final int id = view.getId();

        Session s = Session.getInstance(this);
        if (id == R.id.answer_button) {
            String guess = mAnswerEditText.getText().toString();
            if (AnswerChecker.stripAnswer(guess).equalsIgnoreCase(s_SKIP)) {
                s.skipPuzzle();
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
                startActivity(i);
            }

        } else if (id == R.id.hint_button) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Yay! You hit the hint button!",
                    Toast.LENGTH_SHORT);
            toast.show();
        } else if (id == R.id.answer_log_button) {

            Toast toast = Toast.makeText(getApplicationContext(),
                    "Yay! You hit the answer log button!",
                    Toast.LENGTH_SHORT);
            toast.show();
         /*   if(Session.getInstance(this).login(mTeamEditText.getText().toString(),
                    mPassEditText.getText().toString())) {
                startActivity(new Intent(this, MainActivity.class)
                        .putExtra(Intent.EXTRA_INTENT,
                                getIntent().getParcelableExtra(Intent.EXTRA_INTENT)));
                                */
        } else if (id == R.id.do_puzzle_button) {
            // session will tell us what activity to launch
                    /*   if(Session.getInstance(this).login(mTeamEditText.getText().toString(),
                    mPassEditText.getText().toString())) {
                startActivity(new Intent(this, MainActivity.class)
                        .putExtra(Intent.EXTRA_INTENT,
                                getIntent().getParcelableExtra(Intent.EXTRA_INTENT)));
                                */
            // for now show a toast
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Woot! Prepare for battle!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
