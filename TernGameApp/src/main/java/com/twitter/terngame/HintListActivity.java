package com.twitter.terngame;

import android.app.ListActivity;
import android.os.Bundle;

import com.twitter.terngame.data.HintInfo;

import java.util.ArrayList;


public class HintListActivity extends ListActivity
        implements Session.HintListener {
    public static final String s_puzzleID = "puzzleID";  // intent key

    private String mPuzzleID;
    private HintListArrayAdapter mAdapter;
    private Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hintlist_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPuzzleID = extras.getString(s_puzzleID);
        }

        mSession = Session.getInstance(this);
        ArrayList<HintInfo> hintArray = mSession.getHintStatus(mPuzzleID);

        mAdapter = new HintListArrayAdapter(this, mPuzzleID, hintArray);
        setListAdapter(mAdapter);

        mSession.registerHintListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSession.unregisterHintListener(this);
    }

    public void onHintReady(String puzzleID, String hintID) {
        mAdapter.notifyDataSetChanged();
    }

}
