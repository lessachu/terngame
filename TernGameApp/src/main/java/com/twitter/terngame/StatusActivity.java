package com.twitter.terngame;

import android.os.Bundle;

import com.twitter.terngame.data.TeamStatus;

import java.util.ArrayList;


public class StatusActivity extends BaseListActivity {

    private StatusArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_activity);

        ArrayList<TeamStatus.PuzzleStatus> puzzleArray = new ArrayList<TeamStatus.PuzzleStatus>();
        mAdapter = new StatusArrayAdapter(this, puzzleArray);
        setListAdapter(mAdapter);
    }

    public void showUX() {
        super.showUX();

        mAdapter.clear();
        // construct the array based on the puzzle order
        ArrayList<String> puzzleIDArray = mSession.getPuzzleList();
        for (String puzzleID : puzzleIDArray) {
            TeamStatus.PuzzleStatus ps = mSession.getPuzzleStatus(puzzleID);
            if (ps != null) {
                mAdapter.add(ps);
            }
        }
    }

}
