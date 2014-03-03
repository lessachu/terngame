package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TwittermonActivity extends Activity
        implements View.OnClickListener {

    // Intent keys
//    public static final String s_puzzleID = "puzzleID";
    public static final String s_collected = "collected";

    private String mCollected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.puzzle_activity);

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {

            if (i.hasExtra(s_collected)) {
                mCollected = extras.getString(s_collected);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // goes here because we come back to this Activity a lot
        Session s = Session.getInstance(this);

    }

    @Override
    protected void onStop() {
        super.onStop();

        Session s = Session.getInstance(this);
    }

    public void onClick(View view) {
        final int id = view.getId();

/*        Session s = Session.getInstance(this);
        if (id == R.id.hint_button) {
            Intent i = new Intent(this, HintListActivity.class);
            i.putExtra(HintListActivity.s_puzzleID, mPuzzleID);
            startActivity(i);
        }
*/
    }
}
