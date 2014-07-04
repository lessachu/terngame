package com.twitter.terngame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.terngame.data.TwittermonInfo;
import com.twitter.terngame.util.TwittermonBattleRoyalHelper;

public class TwittermonBattleRoyaleActivity extends Activity
        implements View.OnClickListener {

    private Session mSession;
    //start layout
    private Button mStart;
    private TextView mResultView;
    private Button mShowAnswer;

    // match layout
    private ImageView mImageView;
    private TextView mNameView;
    private ImageView mOppImageView;
    private TextView mOppNameView;
    private TextView mPromptView;
    private TextView mMatchView;
    private Button mWin;
    private Button mLose;
    private Button mTie;
    private TwittermonBattleRoyalHelper mRoyaleHelper;
    private TwittermonInfo.BattleInfo mBattle;
    private LinearLayout mStartLayout;
    private LinearLayout mMatchLayout;
    private LinearLayout mWinLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_royale);
        mSession = Session.getInstance(this);

        mRoyaleHelper = new TwittermonBattleRoyalHelper();
        mRoyaleHelper.setTwittermonInfo(mSession.getPuzzleExtraInfo().getTwittermonInfo());

        mStartLayout = (LinearLayout) findViewById(R.id.start_layout);
        mMatchLayout = (LinearLayout) findViewById(R.id.match_layout);
        mWinLayout = (LinearLayout) findViewById(R.id.win_layout);

        // start layout
        mResultView = (TextView) findViewById(R.id.battle_royale_result);
        mResultView.setVisibility(View.GONE);

        mStart = (Button) findViewById(R.id.start_finale);
        mStart.setOnClickListener(this);

        mShowAnswer = (Button) findViewById(R.id.show_answer);
        mShowAnswer.setOnClickListener(this);

        // match layout
        final RelativeLayout creatureLayout = (RelativeLayout) findViewById(R.id.creature_layout);
        mNameView = (TextView) creatureLayout.findViewById(R.id.twittermon_text);
        mImageView = (ImageView) findViewById(R.id.twittermon_image);

        final RelativeLayout opponentLayout = (RelativeLayout) findViewById(R.id.opponent_layout);
        mOppNameView = (TextView) opponentLayout.findViewById(R.id.twittermon_text);
        mOppImageView = (ImageView) opponentLayout.findViewById(R.id.twittermon_image);

        mPromptView = (TextView) findViewById(R.id.prompt_text);
        mMatchView = (TextView) findViewById(R.id.match_counter_text);

        mWin = (Button) findViewById(R.id.win_button);
        mWin.setOnClickListener(this);

        mLose = (Button) findViewById(R.id.lose_button);
        mLose.setOnClickListener(this);

        mTie = (Button) findViewById(R.id.tie_button);
        mTie.setOnClickListener(this);

        // win layout
        final Button playAgain = (Button) findViewById(R.id.play_again);
        playAgain.setOnClickListener(this);

        if (mSession.isTwittermonRoyaleComplete()) {
            showWinUX();
        } else {
            showStartUX();
        }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        switch (id) {
            case R.id.play_again:
                mRoyaleHelper.clearData();  // deliberate fall through here
            case R.id.start_finale:
                showMatchUX();
                gotoNextBattle();
                break;
            case R.id.win_button:
                logResult(mBattle.mResult == TwittermonInfo.s_win);
                break;
            case R.id.lose_button:
                logResult(mBattle.mResult == TwittermonInfo.s_lose);
                break;
            case R.id.tie_button:
                logResult(mBattle.mResult == TwittermonInfo.s_tie);
                break;
            case R.id.show_answer:
                showWinUX();
                break;
        }
    }

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

            if (numCorrect >= mRoyaleHelper.s_total) {
                mSession.logTwittermonRoyaleComplete();
                showWinUX();
            } else {
                gotoNextBattle();
            }
        } else {
            setResultUX(mRoyaleHelper.getCorrect());
            mRoyaleHelper.clearData();
            showStartUX();
        }
    }

    public void setResultUX(int numCorrect) {
        if (numCorrect < TwittermonBattleRoyalHelper.s_total) {
            String text = "INCORRECT. TRY AGAIN.";
            mResultView.setText(text);
            mResultView.setVisibility(View.VISIBLE);
        }
    }


    private void gotoNextBattle() {
        mBattle = mRoyaleHelper.getMatchup();
        Log.d("terngame", "BattleRoyaleActivity between: " + mBattle.mCreature + " and " + mBattle.mOpponent);

        mNameView.setText(mBattle.mCreature);
        mImageView.setImageDrawable(mSession.getTwittermonImage(mBattle.mCreature));

        mOppNameView.setText(mBattle.mOpponent);
        mOppImageView.setImageDrawable(mSession.getTwittermonImage(mBattle.mOpponent));
        mMatchView.setText("MATCH " + Integer.toString(mRoyaleHelper.getCorrect() + 1));

        mPromptView.setText("Did " + mBattle.mCreature + " win, lose, or tie?");
    }

    private void showStartUX() {
        if (mSession.isTwittermonRoyaleComplete()) {
            mShowAnswer.setVisibility(View.VISIBLE);
        } else {
            mShowAnswer.setVisibility(View.GONE);
        }

        mStartLayout.setVisibility(View.VISIBLE);
        mMatchLayout.setVisibility(View.GONE);
        mWinLayout.setVisibility(View.GONE);
    }

    private void showMatchUX() {
        mStartLayout.setVisibility(View.GONE);
        mMatchLayout.setVisibility(View.VISIBLE);
        mWinLayout.setVisibility(View.GONE);
    }

    private void showWinUX() {
        mStartLayout.setVisibility(View.GONE);
        mMatchLayout.setVisibility(View.GONE);
        mWinLayout.setVisibility(View.VISIBLE);
    }
}
