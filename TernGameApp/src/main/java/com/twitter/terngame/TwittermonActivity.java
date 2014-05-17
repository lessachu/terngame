package com.twitter.terngame;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;

import java.util.ArrayList;

public class TwittermonActivity extends ListActivity
implements View.OnClickListener {

    private ArrayList<String> mTwittermon;

    private TwittermonArrayAdapter mAdapter;

    private LinearLayout mNoTwittermonLayout;
    private TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_activity);

        Session s = Session.getInstance(this);
        PuzzleExtraInfo pei = s.getPuzzleExtraInfo();
        TwittermonInfo ti = pei.getTwittermonInfo();

        mTwittermon = ti.getCollectedList();

        mAdapter = new TwittermonArrayAdapter(this, mTwittermon);
        setListAdapter(mAdapter);

        mTitle = (TextView) findViewById(R.id.twittermon_collection_title_text);

    }

    @Override
    protected void onResume() {
        super.onResume();
/*
        if (mTwittermon.isEmpty()) {
            mNoTwittermonLayout.setVisibility(View.VISIBLE);
            mTitle.setVisibility(View.GONE);
        } else {
            mNoTwittermonLayout.setVisibility(View.GONE);
            mTitle.setVisibility(View.VISIBLE);
        }*/
    }


    public void onTwittermonCollected() {
        mAdapter.notifyDataSetChanged();
    }

    public void onClick(View view) {


    }
}
