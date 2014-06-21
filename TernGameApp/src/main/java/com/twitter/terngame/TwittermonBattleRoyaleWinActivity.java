package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.twitter.terngame.util.TwittermonBattleRoyalHelper;

public class TwittermonBattleRoyaleWinActivity extends Activity {

    public static final String s_correct = "correct";
    public static final String s_total = "total";

    private ImageView mImageView;
    private TextView mAnswerPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_royale_win);

        Session s = Session.getInstance(this);
        Intent i = getIntent();
        Bundle extras = i.getExtras();

        int numWins = 0;
        int numBattles = 0;
        if (extras != null) {
            numWins = extras.getInt(s_correct);
            numBattles = extras.getInt(s_total);
        }

        mImageView = (ImageView) findViewById(R.id.twittermon_image);
        Drawable creatureImage = this.getResources().getDrawable(R.drawable.collect_fail);
        mImageView.setImageDrawable(creatureImage);
        mAnswerPrompt = (TextView) findViewById(R.id.answer_prompt);
        mAnswerPrompt.setText("Great job! You correctly predicted the outcome of  " + Integer.toString(numWins) +
                " of " + Integer.toString(numBattles) + " battles " +
                Integer.toString(TwittermonBattleRoyalHelper.s_total_time) +
                " seconds!\n\nThe answer to this puzzle is:");
    }

}
