package com.twitter.terngame;

import android.os.Bundle;
import android.widget.TextView;

import com.twitter.terngame.data.TwittermonInfo;

import java.util.ArrayList;


public class TwittermonBattleHistoryActivity extends BaseListActivity {

    private TwittermonBattleHistoryArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_activity);

        final TextView title = (TextView) findViewById(R.id.status_title);
        title.setText(this.getString(R.string.battle_history));

        final ArrayList<TwittermonInfo.BattleInfo> battleArray =
                new ArrayList<TwittermonInfo.BattleInfo>();

        mAdapter = new TwittermonBattleHistoryArrayAdapter(this, battleArray);
        setListAdapter(mAdapter);
    }


    public void showUX() {
        super.showUX();

        mAdapter.clear();
        final ArrayList<TwittermonInfo.BattleInfo> battleArray = mSession.getBattleList();
        for (TwittermonInfo.BattleInfo bi : battleArray) {
            mAdapter.add(bi);
        }
    }

}
