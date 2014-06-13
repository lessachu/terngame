package com.twitter.terngame;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.twitter.terngame.data.TwittermonInfo;

import java.util.ArrayList;


public class TwittermonBattleHistoryActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_activity);

        final TextView title = (TextView) findViewById(R.id.status_title);
        title.setText(this.getString(R.string.battle_history));

        Session s = Session.getInstance(this);
        final ArrayList<TwittermonInfo.BattleInfo> battleArray = s.getBattleList();

        final TwittermonBattleHistoryArrayAdapter adapter =
                new TwittermonBattleHistoryArrayAdapter(this, battleArray);
        setListAdapter(adapter);
    }

}
