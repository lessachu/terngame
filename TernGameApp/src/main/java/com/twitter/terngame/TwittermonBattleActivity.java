package com.twitter.terngame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;

public class TwittermonBattleActivity extends Activity {

    public static String s_creature = "creature";

    private Session mSession;
    private String mCreature;
    private TextView mTextView;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_battle);

        mSession = Session.getInstance(this);
        PuzzleExtraInfo pei = mSession.getPuzzleExtraInfo();
        TwittermonInfo ti = pei.getTwittermonInfo();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCreature = extras.getString(s_creature);
        } else {
            mCreature = "error";
        }

        mTextView = (TextView) findViewById(R.id.name_title);
        mTextView.setText(mCreature);

        mImageView = (ImageView) findViewById(R.id.twittermon_image);
        mImageView.setImageDrawable(mSession.getTwittermonImage(mCreature));

    }

    @Override
    protected void onResume() {
        super.onResume();

//        mImageView.setImageDrawable();
    }
}
