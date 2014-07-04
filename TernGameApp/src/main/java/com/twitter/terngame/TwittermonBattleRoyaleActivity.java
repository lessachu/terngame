package com.twitter.terngame;

import android.app.Activity;
import android.content.Intent;
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

    public static final int RESULT_REQUEST_CODE = 1;

    private Session mSession;
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
    private LinearLayout mMatchLayout;
    private LinearLayout mWinLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.twittermon_royale);
        mSession = Session.getInstance(this);

        mRoyaleHelper = new TwittermonBattleRoyalHelper();
        mRoyaleHelper.setTwittermonInfo(mSession.getPuzzleExtraInfo().getTwittermonInfo());

        mMatchLayout = (LinearLayout) findViewById(R.id.match_layout);
        mWinLayout = (LinearLayout) findViewById(R.id.win_layout);

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

        showMatchUX();
        gotoNextBattle();
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        switch (id) {
            case R.id.win_button:
                logResult(mBattle.mResult == TwittermonInfo.s_win);
                break;
            case R.id.lose_button:
                logResult(mBattle.mResult == TwittermonInfo.s_lose);
                break;
            case R.id.tie_button:
                logResult(mBattle.mResult == TwittermonInfo.s_tie);
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
                showWinUX();
            } else {
                gotoNextBattle();
            }
        } else {
            Intent output = new Intent();
            output.putExtra(TwittermonBattleRoyaleStartActivity.s_correct, mRoyaleHelper.getCorrect());
            setResult(RESULT_OK, output);
            finish();
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

    private void showMatchUX() {
        mMatchLayout.setVisibility(View.VISIBLE);
        mWinLayout.setVisibility(View.GONE);
    }

    private void showWinUX() {
        mMatchLayout.setVisibility(View.GONE);
        mWinLayout.setVisibility(View.VISIBLE);
    }
}
