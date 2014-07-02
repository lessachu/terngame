package com.twitter.terngame;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.twitter.terngame.data.AnswerInfo;
import com.twitter.terngame.util.AnswerChecker;

import java.util.ArrayList;

public class PuzzleActivity extends Activity
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

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        mHintPrompt = false;
        if (extras != null) {
            mPuzzleID = extras.getString(s_puzzleID);
            if (i.hasExtra(s_hintPrompt)) {
                mHintPrompt = extras.getBoolean(s_hintPrompt);
            }
        }

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

        if (s.showPuzzleButton(mPuzzleID)) {
            mPuzzleButton.setText(s.getPuzzleButtonText(mPuzzleID));
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
            if (mHintPrompt) {
                mStatusTextView.setText(getString(R.string.hint_prompt));
            }
            setAnswerUIVisibility(View.VISIBLE);
            Log.d("terngame", "Start time as int: " + Integer.toString((int) s.getPuzzleStartTime(mPuzzleID)));
            mPuzzleTimer.setBase(s.getPuzzleStartTime(mPuzzleID));
            mPuzzleTimer.start();
        }

        s.registerHintListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Session s = Session.getInstance(this);
        if (!s.puzzleSkipped(mPuzzleID) && !s.puzzleSolved(mPuzzleID)) {
            mPuzzleTimer.stop();
        }
        s.unregisterHintListener(this);
    }

    public void onHintReady(String puzzleID, String hintID) {
        // TODO: consider when this should go away
        mStatusTextView.setText(getString(R.string.hint_prompt));
    }

    public void setCompletedPuzzleUI(String status_text) {
        Session s = Session.getInstance(this);
        mStatusTextView.setText(status_text);
        setAnswerUIVisibility(View.GONE);

        long timeElapsed = s.getPuzzleEndTime(mPuzzleID) - s.getPuzzleStartTime(mPuzzleID);
        mPuzzleTimer.setBase(SystemClock.elapsedRealtime() - timeElapsed);
    }

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
            if (AnswerChecker.stripAnswer(guess).equalsIgnoreCase(s.getSkipCode())) {
                String answer = s.getCorrectAnswer(mPuzzleID);
                String response = s.skipPuzzle(mPuzzleID);
                mPuzzleTimer.stop();

                Intent i = new Intent(this, GuessActivity.class);
                i.putExtra(GuessActivity.s_is_skip, true);
                i.putExtra(GuessActivity.s_guess_word, answer);
                i.putExtra(GuessActivity.s_response, response);
                i.putExtra(GuessActivity.s_correct, false);
                startActivity(i);

            } else {
                // register a guess
                AnswerInfo ai = s.guessAnswer(guess);
                Intent i = new Intent(this, GuessActivity.class);
                i.putExtra(GuessActivity.s_guess_word, guess);
                i.putExtra(GuessActivity.s_response, ai.mResponse);
                if (ai.mDuplicate) {
                    i.putExtra(GuessActivity.s_duplicate, s.getDuplicateAnswerString());
                }
                i.putExtra(GuessActivity.s_correct, ai.mCorrect);

                if (ai.mCorrect) {
                    mPuzzleTimer.stop();
                }
                startActivity(i);
            }

        } else if (id == R.id.hint_button) {
            Intent i = new Intent(this, HintListActivity.class);
            i.putExtra(HintListActivity.s_puzzleID, mPuzzleID);
            startActivity(i);
        } else if (id == R.id.guess_log_button) {
            ArrayList<String> guesses = s.getGuesses(mPuzzleID);

            if (guesses != null) {
                Intent i = new Intent(this, GuessLogActivity.class);
                i.putExtra(GuessLogActivity.s_guess_key, guesses);
                startActivity(i);
            }
        } else if (id == R.id.do_puzzle_button) {
            NfcManager manager = (NfcManager) this.getSystemService(Context.NFC_SERVICE);
            NfcAdapter adapter = manager.getDefaultAdapter();
            if (adapter != null) {
                if (!adapter.isEnabled()) {
                    showNFCSettingsDialog();
                } else {
                    Intent i = new Intent(this, TwittermonActivity.class);
                    startActivity(i);
                }
            } else {
                showNoNFCDialog();
            }
        }
    }

    public void showNFCSettingsDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("NFC is turned off");
        alertDialogBuilder
                .setMessage("You'll need to enable NFC to complete this puzzle.")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (android.os.Build.VERSION.SDK_INT >= 16) {
                            startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                        } else {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void showNoNFCDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Your phone doesn't support NFC");
        alertDialogBuilder
                .setMessage("You'll need an NFC-enabled phone to complete this puzzle.")
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
