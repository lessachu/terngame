package com.twitter.terngame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.terngame.data.PuzzleExtraInfo;
import com.twitter.terngame.data.TwittermonInfo;
import com.twitter.terngame.util.TwittermonBattleRoyalHelper;

public class TwittermonBattleRoyaleActivity extends BaseActivity
        implements View.OnClickListener {

    private final static String s_royale_helper = "royale_helper";
    private final static String s_creature = "creature";
    private final static String s_opponent = "opponent";
    private final static String s_result = "result";
    private final static String s_state = "state";
    private final static String s_result_prompt = "result_prompt";

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

    // state of the UX
    private int mState;
    private boolean mLoading;  // ugh, I hate that I did this
    private final static int s_start = 0;
    private final static int s_battle = 1;
    private final static int s_won = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_royale);

        mBattle = new TwittermonInfo.BattleInfo(null, null, 0);
        String resultViewText = "";

        if (savedInstanceState != null) {
            mRoyaleHelper = savedInstanceState.getParcelable(s_royale_helper);
            mState = savedInstanceState.getInt(s_state);
            mBattle.mCreature = savedInstanceState.getString(s_creature);
            mBattle.mOpponent = savedInstanceState.getString(s_opponent);
            mBattle.mResult = savedInstanceState.getInt(s_result);
            resultViewText = savedInstanceState.getString(s_result_prompt);

            mLoading = true;
        } else {
            mRoyaleHelper = new TwittermonBattleRoyalHelper();
            mState = s_start;
            mLoading = false;
        }

        mStartLayout = (LinearLayout) findViewById(R.id.start_layout);
        mMatchLayout = (LinearLayout) findViewById(R.id.match_layout);
        mWinLayout = (LinearLayout) findViewById(R.id.win_layout);

        // start layout
        mResultView = (TextView) findViewById(R.id.battle_royale_result);
        mResultView.setVisibility(View.GONE);
        mResultView.setText(resultViewText);

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

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(s_royale_helper, mRoyaleHelper);
        outState.putInt(s_state, mState);
        outState.putString(s_creature, mBattle.mCreature);
        outState.putString(s_opponent, mBattle.mOpponent);
        outState.putInt(s_result, mBattle.mResult);
        outState.putString(s_result_prompt, mResultView.getText().toString());
    }

    @Override
    public void showUX() {
        super.showUX();

        PuzzleExtraInfo pei = mSession.getPuzzleExtraInfo();
        TwittermonInfo ti = pei.getTwittermonInfo();
        mRoyaleHelper.setTwittermonInfo(ti);

        switch (mState) {
            case s_start:
                showStartUX();
                break;
            case s_battle:
                if (mLoading) {
                    gotoNextBattle(mBattle);
                    mLoading = false;
                }
                showMatchUX();
                break;
            case s_won:
                showWinUX();
                break;
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
                gotoNextBattle(null);
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
                gotoNextBattle(null);
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


    private void gotoNextBattle(TwittermonInfo.BattleInfo battle) {

        if (battle == null) {
            mBattle = battle = mRoyaleHelper.getMatchup();
        }
        Log.d("terngame", "BattleRoyaleActivity between: " + battle.mCreature + " and " + battle.mOpponent);

        mNameView.setText(battle.mCreature);
        mImageView.setImageDrawable(mSession.getTwittermonImage(battle.mCreature));

        mOppNameView.setText(battle.mOpponent);
        mOppImageView.setImageDrawable(mSession.getTwittermonImage(battle.mOpponent));
        mMatchView.setText("MATCH " + Integer.toString(mRoyaleHelper.getCorrect() + 1));

        mPromptView.setText("Did " + battle.mCreature + " win, lose, or tie?");
    }

    private void showStartUX() {
        mState = s_start;
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
        mState = s_battle;
        mStartLayout.setVisibility(View.GONE);
        mMatchLayout.setVisibility(View.VISIBLE);
        mWinLayout.setVisibility(View.GONE);
    }

    private void showWinUX() {
        mState = s_won;
        mStartLayout.setVisibility(View.GONE);
        mMatchLayout.setVisibility(View.GONE);
        mWinLayout.setVisibility(View.VISIBLE);
    }
}
