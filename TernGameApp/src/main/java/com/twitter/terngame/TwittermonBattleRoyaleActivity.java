package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private TextView mMatchView;
    private Button mTryAgain;
    private Button mWin;
    private Button mLose;
    private Button mTie;
    private TextView mTimerText;
    private CountDownTimer mTimer;
    private TwittermonBattleRoyalHelper mRoyaleHelper;
    private TwittermonInfo.BattleInfo mBattle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_royale);
        mSession = Session.getInstance(this);

        mRoyaleHelper = new TwittermonBattleRoyalHelper();
        mRoyaleHelper.setTwittermonInfo(mSession.getPuzzleExtraInfo().getTwittermonInfo());

        mTextView = (TextView) findViewById(R.id.royale_title);
        mNameView = (TextView) findViewById(R.id.name_title);
        mImageView = (ImageView) findViewById(R.id.twittermon_image);

        mOppNameView = (TextView) findViewById(R.id.opponent_title);
        mOppImageView = (ImageView) findViewById(R.id.opponent_image);

        mPromptView = (TextView) findViewById(R.id.prompt_text);
        mMatchView = (TextView) findViewById(R.id.match_counter_text);

        mTryAgain = (Button) findViewById(R.id.restart_royale_button);
        mTryAgain.setOnClickListener(this);

        mWin = (Button) findViewById(R.id.win_button);
        mWin.setOnClickListener(this);

        mLose = (Button) findViewById(R.id.lose_button);
        mLose.setOnClickListener(this);

        mTie = (Button) findViewById(R.id.tie_button);
        mTie.setOnClickListener(this);

        mTimerText = (TextView) findViewById(R.id.puzzle_timer);
        initializeTimer();

        gotoNextBattle();
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        switch (id) {
            case R.id.restart_royale_button:
                restartRoyale();
                break;
            case R.id.win_button:
                logResult(mBattle.mResult == TwittermonInfo.s_win);
                gotoNextBattle();
                break;
            case R.id.lose_button:
                logResult(mBattle.mResult == TwittermonInfo.s_lose);
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
            final int numCorrect = mRoyaleHelper.getCorrect();
            String yay = "Correct!";
            if (numCorrect < mRoyaleHelper.s_total) {
                yay += " " + Integer.toString(mRoyaleHelper.s_total - numCorrect) + " to go!";
            }
            Toast toast = Toast.makeText(getApplicationContext(),
                    yay,
                    Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Nope!  Try again!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void initializeTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new CountDownTimer(TwittermonBattleRoyalHelper.s_total_time * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTimerText.setText(Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                mTimerText.setText("Time's Up!");
                if (mRoyaleHelper.getCorrect() >= TwittermonBattleRoyalHelper.s_total) {
                    Intent i = new Intent(TwittermonBattleRoyaleActivity.this, TwittermonBattleRoyaleWinActivity.class);
                    i.putExtra(TwittermonBattleRoyaleWinActivity.s_correct, mRoyaleHelper.getCorrect());
                    i.putExtra(TwittermonBattleRoyaleWinActivity.s_total, mRoyaleHelper.getTotal());
                    startActivity(i);
                } else {
                    // put up a "not good enough message"
                }
            }
        }.start();
    }

    private void gotoNextBattle() {
        mBattle = mRoyaleHelper.getMatchup();
        Log.d("terngame", "BattleRoyaleActivity between: " + mBattle.mCreature + " and " + mBattle.mOpponent);

        mNameView.setText(mBattle.mCreature);
        mImageView.setImageDrawable(mSession.getTwittermonImage(mBattle.mCreature));

        mOppNameView.setText(mBattle.mOpponent);
        mOppImageView.setImageDrawable(mSession.getTwittermonImage(mBattle.mOpponent));
        mMatchView.setText(Integer.toString(mRoyaleHelper.getCorrect()) + " OF " +
                Integer.toString(mRoyaleHelper.getTotal()) + " CORRECT");

        mPromptView.setText("Did " + mBattle.mCreature + " win, lose, or tie?");
    }

    private void restartRoyale() {
        mRoyaleHelper.clearData();
        initializeTimer();
        // Toast messaging
        gotoNextBattle();
    }
}
