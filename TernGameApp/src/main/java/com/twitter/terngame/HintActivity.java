package com.twitter.terngame;

import android.app.ListActivity;
import android.os.Bundle;

import com.twitter.terngame.data.HintInfo;

import java.util.ArrayList;


public class HintActivity extends ListActivity {
    public static String s_puzzleID = "puzzleID";  // intent key

    private String mPuzzleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hint_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPuzzleID = extras.getString(s_puzzleID);
        }

        Session s = Session.getInstance(this);
        ArrayList<HintInfo> hintArray = s.getHintStatus(mPuzzleID);

        final HintArrayAdapter adapter = new HintArrayAdapter(this, hintArray);
        setListAdapter(adapter);
    }

}
