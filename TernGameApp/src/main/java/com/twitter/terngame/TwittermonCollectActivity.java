package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TwittermonCollectActivity extends Activity
        implements View.OnClickListener {

    // Intent keys
    public static final String s_collected = "collected";

    private String mCollected;
    private ArrayList<String> mTwittermon;

    private LinearLayout mNoTwittermonLayout;
    private TextView mTitle;

    // debug - just to test failing
    private Button mFailButton;
    private Button mDupeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.twittermon_activity);

        mTwittermon = new ArrayList<String>();
        mTwittermon.add("rockdove"); // debug

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {

            if (i.hasExtra(s_collected)) {
                mCollected = extras.getString(s_collected);

                // TODO: convert mCollected to mTwittermon

            }
        }

        mNoTwittermonLayout = (LinearLayout) findViewById(R.id.no_twittermon_layout);
        mTitle = (TextView) findViewById(R.id.twittermon_collection_title_text);

        //debug
        mFailButton = (Button) findViewById(R.id.fail_button);
        mFailButton.setOnClickListener(this);

        mDupeButton = (Button) findViewById(R.id.dupe_button);
        mDupeButton.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session s = Session.getInstance(this);

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
        }

    }
}
