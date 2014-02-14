package com.twitter.terngame;

import android.app.Activity;
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
public class AdminActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_activity);

        Session s = Session.getInstance(this);

        Button clearAllButton = (Button) findViewById(R.id.admin_clear_all_button);
        clearAllButton.setOnClickListener(this);

        Spinner spinner = (Spinner) findViewById(R.id.current_puzzle_spinner);
        ArrayList<String> puzzles = s.getPuzzleList();
        final String none = getString(R.string.None);
        puzzles.add(none);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, puzzles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final String curPuzzle = s.getCurrentPuzzleID();
        int selectionPos;
        if (curPuzzle != null) {
            selectionPos = adapter.getPosition(curPuzzle);
        } else {
            selectionPos = adapter.getPosition(none);
        }

        spinner.setSelection(selectionPos);
        spinner.setOnItemSelectedListener(this);

        // TODO: populate the current puzzle spinner
        // TODO: hook up the puzzle and skip count number selectors
        // TODO: add UX to edit puzzle data
    }

    public void onItemSelected(AdapterView<?> parent, View view,
            int pos, long id) {
        String selection = (String) parent.getItemAtPosition(pos);
        final String none = getString(R.string.None);

        Session s = Session.getInstance(this);
        if (selection != null && selection.equals(none)) {
            s.clearCurrentPuzzle();
        } else {
            s.isValidStartCode(selection);
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
            // TODO: session should clear all data

            s.clearTeamData();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Team data wiped.",
                    Toast.LENGTH_SHORT);
            toast.show();

        }
    }
}
