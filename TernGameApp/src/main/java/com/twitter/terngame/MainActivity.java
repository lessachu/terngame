package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.terngame.util.AnswerChecker;

/**
 * Created by jchong on 1/12/14.
 */
public class MainActivity extends Activity
        implements View.OnClickListener {

    private TextView mEventNameText;
    private EditText mStartCodeEditText;
    private Button mCurPuzzleButton;
    private Button mSolvedStatusButton;
    private TextView mInstructionText;

    private static String s_admin_mode = "start admin mode";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_main);
        Session s = Session.getInstance(this);

        mEventNameText = (TextView) findViewById(R.id.event_name_text);
        final String eventName = s.getEventName();
        if (eventName != null) {
            mEventNameText.setText(eventName);
        }

        final TextView teamNameTextView = (TextView) findViewById(R.id.team_name_text);
        teamNameTextView.setText(s.getTeamName());

        final Button goButton = (Button) findViewById(R.id.go_button);
        goButton.setOnClickListener(this);
        goButton.setEnabled(false);

        mInstructionText = (TextView) findViewById(R.id.instruction_text);

        mCurPuzzleButton = (Button) findViewById(R.id.current_puzzle_button);
        mCurPuzzleButton.setOnClickListener(this);

        mSolvedStatusButton = (Button) findViewById(R.id.event_status_button);
        mSolvedStatusButton.setOnClickListener(this);

        mStartCodeEditText = (EditText) findViewById(R.id.start_code_edit);
        mStartCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {
                goButton.setEnabled(text.length() > 0);
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
        Session s = Session.getInstance(this);

        mInstructionText.setText(s.getCurrentInstruction());

        int numSolved = s.getPuzzlesSolved();
        mSolvedStatusButton.setText(Integer.toString(numSolved) + " puzzle" +
                (numSolved == 1 ? "" : "s") + " solved");

        if (s.puzzleStarted()) {
            // put the current puzzle name in there
            mCurPuzzleButton.setText(s.getPuzzleName(s.getCurrentPuzzleID()));
            mCurPuzzleButton.setVisibility(View.VISIBLE);
        } else {
            mCurPuzzleButton.setVisibility(View.GONE);
        }
    }

    public void onClick(View view) {
        final int id = view.getId();

        Session s = Session.getInstance(this);

        if (id == R.id.go_button) {
            final String startcode = mStartCodeEditText.getText().toString();

            if (startcode.equalsIgnoreCase(s_admin_mode)) {
                startActivity(new Intent(this, AdminActivity.class));
            } else if (s.isValidStartCode(startcode)) {
                Intent i = new Intent(this, PuzzleActivity.class);
                i.putExtra("puzzleID", AnswerChecker.stripAnswer(startcode));
                startActivity(i);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "That's not the start code that you're looking for.",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (id == R.id.current_puzzle_button) {
            if (s.puzzleStarted()) {
                Intent i = new Intent(this, PuzzleActivity.class);
                i.putExtra("puzzleID", s.getCurrentPuzzleID());
                startActivity(i);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Hrm... you should not have been able to click this button. Odd",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (id == R.id.event_status_button) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "yay!  You clicked the solved status button!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
