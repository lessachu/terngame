package com.twitter.terngame;

import android.os.Bundle;
import android.util.Log;

import com.twitter.terngame.data.HintInfo;

import java.util.ArrayList;


public class HintListActivity extends BaseListActivity
implements Session.HintListener {
    public static final String s_puzzleID = "puzzleID";  // intent key

    private String mPuzzleID;
    private HintListArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hintlist_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPuzzleID = extras.getString(s_puzzleID);
        }

        ArrayList<HintInfo> hintArray = new ArrayList<HintInfo>();

        mAdapter = new HintListArrayAdapter(this, mPuzzleID, hintArray);
        setListAdapter(mAdapter);
        mSession.registerHintListener(this);
    }

    public void showUX() {
        super.showUX();

        mAdapter.clear();
        ArrayList<HintInfo> hintArray = mSession.getHintStatus(mPuzzleID);
        for (HintInfo hi : hintArray) {
            mAdapter.add(hi);
        }

        Log.d("terngame", "ShowUX: hint count: " + mSession.getHintStatus(mPuzzleID).size());
        Log.d("terngame", "ShowUX: puzzleID: " + mPuzzleID);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSession.unregisterHintListener(this);
    }

    public void onHintReady(String puzzleID, String hintID, int notifID) {
        mAdapter.notifyDataSetChanged();
    }

}
