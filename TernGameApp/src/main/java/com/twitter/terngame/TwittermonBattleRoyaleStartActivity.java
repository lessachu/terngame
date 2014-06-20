package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TwittermonBattleRoyaleStartActivity extends Activity
        implements View.OnClickListener {

    private Button mStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_royale_start);

        mStart = (Button) findViewById(R.id.start_finale);
        mStart.setOnClickListener(this);
    }

    @Override

    public void onClick(View view) {
        final int id = view.getId();

        if (id == R.id.start_finale) {
            Intent i = new Intent(this, TwittermonBattleRoyaleActivity.class);
            startActivity(i);
        }
    }

}
