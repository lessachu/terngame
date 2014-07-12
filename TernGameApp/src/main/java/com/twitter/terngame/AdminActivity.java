package com.twitter.terngame;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jchong on 2/11/14.
 */
public class AdminActivity extends BaseActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    static private String s_none = "None";

    private Button mClearOneButton;
    private Spinner mPuzzleSpinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_activity);

        Button clearAllButton = (Button) findViewById(R.id.admin_clear_all_button);
        clearAllButton.setOnClickListener(this);

        mClearOneButton = (Button) findViewById(R.id.admin_clear_one_button);
        mClearOneButton.setOnClickListener(this);

        mPuzzleSpinner = (Spinner) findViewById(R.id.current_puzzle_spinner);
        mPuzzleSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void showUX() {
        super.showUX();
        ArrayList<String> puzzles = mSession.getPuzzleList();
        puzzles.add(s_none);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, puzzles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPuzzleSpinner.setAdapter(adapter);

        final String curPuzzle = mSession.getCurrentPuzzleID();
        int selectionPos;
        if (curPuzzle != null) {
            selectionPos = adapter.getPosition(curPuzzle);
            mClearOneButton.setEnabled(true);
        } else {
            selectionPos = adapter.getPosition(s_none);
            mClearOneButton.setEnabled(false);
        }

        mPuzzleSpinner.setSelection(selectionPos);
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

        if (id == R.id.admin_clear_all_button) {
            // TODO: show an "are you sure? prompt?
            mSession.clearTeamData();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Team data wiped.",
                    Toast.LENGTH_SHORT);
            toast.show();

        } else if (id == R.id.admin_clear_one_button) {
            String puzzleID = (String) mPuzzleSpinner.getSelectedItem();

            if (puzzleID != null && !puzzleID.equals(s_none)) {
                mSession.clearPuzzleData(puzzleID);
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Puzzle data wiped for " + puzzleID + ".",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
