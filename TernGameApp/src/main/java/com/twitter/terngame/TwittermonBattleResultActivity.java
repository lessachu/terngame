package com.twitter.terngame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;

public class TwittermonBattleResultActivity extends Activity {

    public static String s_creature = "creature";
    public static String s_oppCreature = "oppCreature";

    private Session mSession;
    private String mCreature;
    private String mOpponentCreature;
    private TextView mTextView;
    private ImageView mImageView;
    private TextView mNameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_battle_result);

        Log.d("terngame", "In BattleREsultActivity");

        mSession = Session.getInstance(this);
        PuzzleExtraInfo pei = mSession.getPuzzleExtraInfo();
        TwittermonInfo ti = pei.getTwittermonInfo();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCreature = extras.getString(s_creature);
            mOpponentCreature = extras.getString(s_oppCreature);
        } else {
            mCreature = "error";
            mOpponentCreature = "error";
        }

        mTextView = (TextView) findViewById(R.id.battle_result);

        if (mCreature.equals(mOpponentCreature)) {
            mTextView.setText("It's a tie!");
        } else {
            // TODO: fix this;
            mTextView.setText("You win!");
        }

        mNameView = (TextView) findViewById(R.id.name_title);
        mNameView.setText(mCreature);

        mImageView = (ImageView) findViewById(R.id.twittermon_image);
        mImageView.setImageDrawable(mSession.getTwittermonImage(mCreature));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Session s = Session.getInstance(this);

        String curPuzzle = s.getCurrentPuzzleID();

        Log.d("terngame", "TwittermonBattleResultActivity onResume");
/*
        if (curPuzzle != null && curPuzzle.equals(PuzzleExtraInfo.s_twittermon)) {

            mCollectLayout.setVisibility(View.VISIBLE);
            mNotStartedLayout.setVisibility(View.GONE);
        } else {
            mCollectLayout.setVisibility(View.GONE);
            mNotStartedLayout.setVisibility(View.VISIBLE);
        } */


    }

}
