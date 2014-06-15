package com.twitter.terngame;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;

import java.util.ArrayList;

public class TwittermonActivity extends Activity
        implements View.OnClickListener {

    private ArrayList<String> mTwittermon;

    private TwittermonArrayAdapter mAdapter;
    private FragmentManager mFragmentManager;
    private TwittermonGridFragment mGridFragment;

    private LinearLayout mNoTwittermonLayout;
    private TextView mTitle;
    private FrameLayout mBattleBar;
    private Button mHistoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_activity);

        Session s = Session.getInstance(this);
        PuzzleExtraInfo pei = s.getPuzzleExtraInfo();
        TwittermonInfo ti = pei.getTwittermonInfo();

        final Context context = this;

        mTwittermon = ti.getCollectedList();
        mAdapter = new TwittermonArrayAdapter(this, mTwittermon);

        View.OnClickListener clickListener = new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(context, TwittermonBattleActivity.class);
                i.putExtra(TwittermonBattleActivity.s_creature, (String) v.getTag(R.id.grid_name));
                context.startActivity(i);
            }
        };

        mFragmentManager = getFragmentManager();
        mGridFragment = (TwittermonGridFragment) mFragmentManager.findFragmentById(R.id.twittermon_grid);
        mGridFragment.setClickListener(clickListener);

        mBattleBar = (FrameLayout) findViewById(R.id.battle_bar);
        mHistoryButton = (Button) findViewById(R.id.battle_history);
        mHistoryButton.setOnClickListener(this);

        mTitle = (TextView) findViewById(R.id.twittermon_collection_title_text);
        mNoTwittermonLayout = (LinearLayout) findViewById(R.id.empty_view);

    }

    @Override
    protected void onResume() {
        super.onResume();

        FragmentTransaction ft = mFragmentManager.beginTransaction();

        if (mTwittermon.isEmpty()) {
            mNoTwittermonLayout.setVisibility(View.VISIBLE);
            ft.hide(mGridFragment);
            mTitle.setVisibility(View.GONE);
            mBattleBar.setVisibility(View.GONE);
        } else {
            mNoTwittermonLayout.setVisibility(View.GONE);
            ft.show(mGridFragment);
            mTitle.setVisibility(View.VISIBLE);
            mBattleBar.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
        }
        ft.commit();
    }


    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.battle_history) {
            Intent i = new Intent(this, TwittermonBattleHistoryActivity.class);
            startActivity(i);
        }
    }
}
