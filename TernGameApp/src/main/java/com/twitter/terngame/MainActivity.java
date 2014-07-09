package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.terngame.util.AnswerChecker;

/**
 * created by jchong on 1/12/14.
 */
public class MainActivity extends Activity
        implements View.OnClickListener, Session.DataLoadedListener {

    private Session mSession;
    private TextView mEventNameText;
    private TextView mTeamNameText;
    private EditText mStartCodeEditText;
    private Button mCurPuzzleButton;
    private Button mSolvedStatusButton;
    private TextView mInstructionText;
    private Button mGoButton;
    private LinearLayout mContent;
    private LinearLayout mLoadingLayout;

    private static String s_admin_mode = "start admin mode";
    private static final String s_teamname = "teamname";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("terngame", "Main activity on create callsed");

        setContentView(R.layout.event_main);
        mSession = Session.getInstance(this);

        if (savedInstanceState != null) {
            String teamname = savedInstanceState.getString(s_teamname);
            mSession.restoreLogin(teamname);
        }

        mContent = (LinearLayout) findViewById(R.id.main_content);
        mLoadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        mEventNameText = (TextView) findViewById(R.id.event_name_text);
        mTeamNameText = (TextView) findViewById(R.id.team_name_text);

        mGoButton = (Button) findViewById(R.id.go_button);
        mGoButton.setOnClickListener(this);
        mGoButton.setEnabled(false);

        mInstructionText = (TextView) findViewById(R.id.instruction_text);

        mCurPuzzleButton = (Button) findViewById(R.id.current_puzzle_button);
        mCurPuzzleButton.setOnClickListener(this);

        mSolvedStatusButton = (Button) findViewById(R.id.event_status_button);
        mSolvedStatusButton.setOnClickListener(this);

        mStartCodeEditText = (EditText) findViewById(R.id.start_code_edit);
        mStartCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable text) {
                if (mSession.puzzleStarted()) {
                    mGoButton.setEnabled(text.toString().equalsIgnoreCase(s_admin_mode));
                } else {
                    mGoButton.setEnabled(text.length() > 0);
                }
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

        Log.d("terngame", "MainActivity OnResume called");
        if (mSession.isDataLoaded(this)) {
            showUX();
        } else {
            mLoadingLayout.setVisibility(View.VISIBLE);
            mContent.setVisibility(View.GONE);
        }
    }

    public void showUX() {
        Log.d("terngame", "showUX");
        mLoadingLayout.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);

        final String eventName = mSession.getEventName();
        if (eventName != null) {
            mEventNameText.setText(eventName);
        }
        mTeamNameText.setText(mSession.getTeamName());
        mInstructionText.setText(mSession.getCurrentInstruction());

        int numSolved = mSession.getPuzzlesSolved();
        int numSkipped = mSession.getPuzzlesSkipped();
        String statusString = Integer.toString(numSolved) + " puzzle" +
                (numSolved == 1 ? "" : "s") + " solved";
        if (numSkipped > 0) {
            statusString += "\n" + Integer.toString(numSkipped) + " puzzle" +
                    (numSkipped == 1 ? "" : "s") + " skipped";
        }
        mSolvedStatusButton.setText(statusString);

        if (mSession.puzzleStarted()) {
            // put the current puzzle name in there
            mCurPuzzleButton.setText(mSession.getPuzzleName(mSession.getCurrentPuzzleID()));
            mCurPuzzleButton.setVisibility(View.VISIBLE);
            mGoButton.setEnabled(false);
        } else {
            mCurPuzzleButton.setVisibility(View.GONE);
            mGoButton.setEnabled(mStartCodeEditText.getText().length() > 0);
        }
    }

    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.go_button) {
            final String startcode = mStartCodeEditText.getText().toString();
            mStartCodeEditText.setText("");

            if (startcode.equalsIgnoreCase(s_admin_mode)) {
                startActivity(new Intent(this, AdminActivity.class));
            } else {
                if (mSession.isValidStartCode(startcode)) {
                    mSession.startPuzzle(startcode);
                    Intent i = new Intent(this, PuzzleActivity.class);
                    i.putExtra(PuzzleActivity.s_puzzleID, AnswerChecker.stripAnswer(startcode));
                    startActivity(i);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "That's not the start code that you're looking for.",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        } else if (id == R.id.current_puzzle_button) {
            if (mSession.puzzleStarted()) {
                Intent i = new Intent(this, PuzzleActivity.class);
                i.putExtra(PuzzleActivity.s_puzzleID, mSession.getCurrentPuzzleID());
                startActivity(i);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Hrm... you should not have been able to click this button. Odd",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (id == R.id.event_status_button) {
            Intent i = new Intent(this, StatusActivity.class);
            // construct a list of puzzle items
            startActivity(i);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(s_teamname, mSession.getTeamName());
    }

    public void onDataLoaded() {
        Log.d("terngame", "MainActivity in onDataLoaded, restoring normal UX");
        showUX();
    }

}
