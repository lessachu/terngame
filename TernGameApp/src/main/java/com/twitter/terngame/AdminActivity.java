package com.twitter.terngame;

import android.app.Activity;
import android.app.PendingIntent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.twitter.terngame.util.HintNotification;

import java.util.ArrayList;

/**
 * Created by jchong on 2/11/14.
 */
public class AdminActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    static private String s_none = "None";

    public PendingIntent mPI;
    private Button mClearOneButton;
    private Spinner mPuzzleSpinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_activity);

        Session s = Session.getInstance(this);

        Button clearAllButton = (Button) findViewById(R.id.admin_clear_all_button);
        clearAllButton.setOnClickListener(this);

        mClearOneButton = (Button) findViewById(R.id.admin_clear_one_button);
        mClearOneButton.setOnClickListener(this);

        Button testNotificationButton = (Button) findViewById(R.id.test_notification_button);
        testNotificationButton.setOnClickListener(this);

        Button cancelNotificationButton = (Button) findViewById(R.id.cancel_hints_button);
        cancelNotificationButton.setOnClickListener(this);

        mPuzzleSpinner = (Spinner) findViewById(R.id.current_puzzle_spinner);
        ArrayList<String> puzzles = s.getPuzzleList();
        puzzles.add(s_none);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, puzzles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPuzzleSpinner.setAdapter(adapter);

        final String curPuzzle = s.getCurrentPuzzleID();
        int selectionPos;
        if (curPuzzle != null) {
            selectionPos = adapter.getPosition(curPuzzle);
            mClearOneButton.setEnabled(true);
        } else {
            selectionPos = adapter.getPosition(s_none);
            mClearOneButton.setEnabled(false);
        }

        mPuzzleSpinner.setSelection(selectionPos);
        mPuzzleSpinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
            int pos, long id) {
        String selection = (String) parent.getItemAtPosition(pos);

        if (selection != null && !selection.equals(s_none)) {
            mClearOneButton.setEnabled(true);
        } else {
            mClearOneButton.setEnabled(false);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void onClick(View view) {
        final int id = view.getId();

        Session s = Session.getInstance(this);
        if (id == R.id.admin_clear_all_button) {
            // TODO: show an "are you sure? prompt?
            s.clearTeamData();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Team data wiped.",
                    Toast.LENGTH_SHORT);
            toast.show();

        } else if (id == R.id.admin_clear_one_button) {
            String puzzleID = (String) mPuzzleSpinner.getSelectedItem();

            if (puzzleID != null && !puzzleID.equals(s_none)) {
                s.clearPuzzleData(puzzleID);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Puzzle data wiped for " + puzzleID + ".",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        } else if (id == R.id.test_notification_button) {
            mPI = HintNotification.scheduleHint(this, "wombat", 1, "hintID", 15);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Launching hint notification in 15 seconds.",
                    Toast.LENGTH_SHORT);
            toast.show();

        } else if (id == R.id.cancel_hints_button) {
            HintNotification.cancelHintAlarms(this, mPI);
        }
    }
}
