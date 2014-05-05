package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;

import java.util.ArrayList;

public class TwittermonActivity extends Activity
        implements View.OnClickListener {

    private String mCollected;
    private ArrayList<String> mTwittermon;

    private LinearLayout mNoTwittermonLayout;
    private TextView mTitle;

    // debug - just to test failing
    private Button mFailButton;
    private Button mDupeButton;
    private Button mSucceedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_activity);

        mNoTwittermonLayout = (LinearLayout) findViewById(R.id.no_twittermon_layout);
        mTitle = (TextView) findViewById(R.id.twittermon_collection_title_text);

        //debug
        mFailButton = (Button) findViewById(R.id.fail_button);
        mFailButton.setOnClickListener(this);

        mDupeButton = (Button) findViewById(R.id.dupe_button);
        mDupeButton.setOnClickListener(this);

        mSucceedButton = (Button) findViewById(R.id.succeed_button);
        mSucceedButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session s = Session.getInstance(this);
        PuzzleExtraInfo pei = s.getPuzzleExtraInfo();
        TwittermonInfo ti = pei.getTwittermonInfo();

        mTwittermon = ti.getCollectedList();
        mTwittermon.add("rockdove"); // debug

        if (mTwittermon.isEmpty()) {
            mNoTwittermonLayout.setVisibility(View.VISIBLE);
            mTitle.setVisibility(View.GONE);
        } else {
            mNoTwittermonLayout.setVisibility(View.GONE);
            mTitle.setVisibility(View.VISIBLE);
        }
    }


    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.fail_button) {
            Intent i = new Intent(this, TwittermonCollectFailActivity.class);
            startActivity(i);
        } else if (id == R.id.dupe_button) {
            Intent i = new Intent(this, TwittermonCollectDupeActivity.class);
            i.putExtra(TwittermonCollectDupeActivity.s_creature, "rockdove");
            startActivity(i);
        } else if (id == R.id.succeed_button) {
            Intent i = new Intent(this, TwittermonCollectSucceedActivity.class);
            i.putExtra(TwittermonCollectSucceedActivity.s_creature, "rockdove");
            startActivity(i);
        }

    }
}
