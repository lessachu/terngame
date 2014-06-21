package com.twitter.terngame;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TwittermonBattleRoyaleWinActivity extends Activity {

    private ImageView mImageView;
    private TextView mAnswerPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_royale_win);

        mImageView = (ImageView) findViewById(R.id.twittermon_image);
        Drawable creatureImage = this.getResources().getDrawable(R.drawable.collect_fail);
        mImageView.setImageDrawable(creatureImage);
        mAnswerPrompt = (TextView) findViewById(R.id.answer_prompt);
        mAnswerPrompt.setText("Great job!\n\nThe answer to this puzzle is:");
    }

}
