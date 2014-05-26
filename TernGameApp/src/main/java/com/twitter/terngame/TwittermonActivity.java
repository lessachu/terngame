package com.twitter.terngame;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;

import java.util.ArrayList;

public class TwittermonActivity extends Activity {

    private ArrayList<String> mTwittermon;

    private TwittermonArrayAdapter mAdapter;
    private GridView mGridView;

    private LinearLayout mNoTwittermonLayout;
    private TextView mTitle;
    private FrameLayout mBattleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_activity);

        Session s = Session.getInstance(this);
        PuzzleExtraInfo pei = s.getPuzzleExtraInfo();
        TwittermonInfo ti = pei.getTwittermonInfo();

        mTwittermon = ti.getCollectedList();
        mAdapter = new TwittermonArrayAdapter(this, mTwittermon);

        mGridView = (GridView) findViewById(R.id.twittermon_grid);
        mGridView.setAdapter(mAdapter);

        mBattleBar = (FrameLayout) findViewById(R.id.battle_bar);

        mTitle = (TextView) findViewById(R.id.twittermon_collection_title_text);
        mNoTwittermonLayout = (LinearLayout) findViewById(R.id.empty_view);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTwittermon.isEmpty()) {
            mNoTwittermonLayout.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
            mTitle.setVisibility(View.GONE);
            mBattleBar.setVisibility(View.GONE);
        } else {
            mNoTwittermonLayout.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
            mTitle.setVisibility(View.VISIBLE);
            mBattleBar.setVisibility(View.VISIBLE);
        }
    }

    public void onTwittermonCollected() {
        mAdapter.notifyDataSetChanged();
    }

}
