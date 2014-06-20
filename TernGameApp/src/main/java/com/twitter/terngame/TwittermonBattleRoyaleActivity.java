package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.terngame.data.TwittermonInfo;
import com.twitter.terngame.util.TwittermonBattleRoyalHelper;

public class TwittermonBattleRoyaleActivity extends Activity
        implements View.OnClickListener {

    public static final String s_helper = "helper";

    private Session mSession;
    private TextView mTextView;
    private ImageView mImageView;
    private TextView mNameView;
    private ImageView mOppImageView;
    private TextView mOppNameView;
    private TextView mPromptView;
    private Button mTryAgain;
    private Button mWin;
    private Button mLose;
    private Button mTie;
    private Chronometer mBattleTimer;
    private TwittermonBattleRoyalHelper mRoyaleHelper;
    private TwittermonInfo.BattleInfo mBattle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_battle_royale);
        mSession = Session.getInstance(this);
        mRoyaleHelper = new TwittermonBattleRoyalHelper(mSession.getPuzzleExtraInfo().getTwittermonInfo());


        // TODO: HACKY
        int num_correct = 0;
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        if (extras != null) {
            if (i.hasExtra(s_helper)) {
                num_correct = extras.getInt(s_helper);
            }
        }

        mRoyaleHelper.setCorrect(num_correct);

        mTextView = (TextView) findViewById(R.id.royale_title);
        mNameView = (TextView) findViewById(R.id.name_title);
        mImageView = (ImageView) findViewById(R.id.twittermon_image);

        mOppNameView = (TextView) findViewById(R.id.opponent_title);
        mOppImageView = (ImageView) findViewById(R.id.opponent_image);

        mPromptView = (TextView) findViewById(R.id.prompt_text);

        mTryAgain = (Button) findViewById(R.id.restart_royale_button);
        mTryAgain.setOnClickListener(this);

        mWin = (Button) findViewById(R.id.win_button);
        mWin.setOnClickListener(this);

        mLose = (Button) findViewById(R.id.lose_button);
        mLose.setOnClickListener(this);

        mTie = (Button) findViewById(R.id.tie_button);
        mTie.setOnClickListener(this);

        mBattleTimer = (Chronometer) findViewById(R.id.puzzle_chronometer);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBattle = mRoyaleHelper.getMatchup();
        Log.d("terngame", "BattleRoyaleActivity between: " + mBattle.mCreature + " and " + mBattle.mOpponent);

        mNameView.setText(mBattle.mCreature);
        mImageView.setImageDrawable(mSession.getTwittermonImage(mBattle.mCreature));

        mOppNameView.setText(mBattle.mOpponent);
        mOppImageView.setImageDrawable(mSession.getTwittermonImage(mBattle.mOpponent));

        mPromptView.setText("Did " + mBattle.mCreature + " win, lose, or tie?");
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        switch (id) {
            case R.id.restart_royale_button:
            case R.id.win_button:
                logResult(mBattle.mResult == TwittermonInfo.s_win);
                gotoNextBattle();
                break;
            case R.id.lose_button:
                gotoNextBattle();
                break;
            case R.id.tie_button:
                logResult(mBattle.mResult == TwittermonInfo.s_tie);
                gotoNextBattle();
                break;
        }
    }

    // TODO: jan, add sound effects?
    private void logResult(boolean correct) {
        if (correct) {
            mRoyaleHelper.logCorrect();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Correct! " + Integer.toString(mRoyaleHelper.s_total - mRoyaleHelper.getCorrect()) + " to go!",
                    Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Nope!  Try again!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    private void gotoNextBattle() {
        finish();
        if (mRoyaleHelper.getCorrect() == TwittermonBattleRoyalHelper.s_total) {

        } else {
            Intent i = getIntent();
            // temp hack
            i.putExtra(s_helper, mRoyaleHelper.getCorrect());
            //        i.putExtra(s_helper, mRoyaleHelper);
            startActivity(i);
        }
    }
}
